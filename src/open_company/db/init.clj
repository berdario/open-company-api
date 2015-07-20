(ns open-company.db.init
  "Initialize RethinkDB with tables and indexs."
  (:require [rethinkdb.query :as r]
            [open-company.config :as c]))

(defn create-database
  "Create a RethinkDB database, catching the exception for it already existing and returning truthy anyway."
  [conn db-name]
  (try
    (r/run (r/db-create db-name) conn)
    (catch java.lang.Exception e
      (re-find #" already exists.$" (.getMessage e)))))

(defn table-list
  "Return a sequence of the table names in the RethinkDB."
  [conn db-name]
  (-> (r/db db-name) (r/table-list) (r/run conn)))

(defn create-table
  "Create a RethinkDB table with the specified primary key if it doesn't exist."
  [conn db-name table-name primary-key]
  (when (not-any? #(= table-name %) (table-list conn db-name))
    (-> (r/db db-name)
      (r/table-create table-name {:primary-key primary-key :durability "hard"})
      (r/run conn))))

(defn index-list
  "Return a sequence of the index names for a table in the RethinkDB."
  [conn table-name]
  (-> (r/table table-name) (r/index-list) (r/run conn)))

(defn create-index
  "Create RethinkDB table index for the specified field if it doesn't exist."
  [conn table-name index-name]
  (when (not-any? #(= index-name %) (index-list conn table-name))
    (-> (r/table table-name)
      (r/index-create index-name 
        (r/fn [row]
          (r/get-field row (keyword index-name))))
      (r/run conn))
    (-> (r/table table-name)
      (r/index-wait index-name)
      (r/run conn))))

(defn init []
  (let [db-name c/db-name]
    (println (str "\nOpen Company: Initializing database - " db-name))
    (with-open [conn (apply r/connect c/db-options)]
      (when (create-database conn db-name) 
        (do 
          (print ".")
          (create-table conn db-name "companies" "symbol") (print ".")
          (create-table conn db-name "reports" "symbol-year-period") (print ".")
          (create-index conn "reports" "symbol") (print ".")
          (println "\nOpen Company: Database initialization complete - " db-name "\n"))))))

(defn -main []
  (init))