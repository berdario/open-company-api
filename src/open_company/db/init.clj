(ns open-company.db.init
  "Initialize RethinkDB with tables and indexs."
  (:require [rethinkdb.query :as r]
            [open-company.config :as c]
            [open-company.resources.company :as company]
            [open-company.resources.section :as section]))

(defn- create-database
  "Create a RethinkDB database if it doesn't already exist."
  [conn db-name]
  (if-let [db-list (r/run (r/db-list) conn)]
    (if (some #(= db-name %) db-list)
      true ; already exists, return truthy
      (r/run (r/db-create db-name) conn))))

(defn- table-list
  "Return a sequence of the table names in the RethinkDB."
  [conn db-name]
  (-> (r/db db-name) (r/table-list) (r/run conn)))

(defn- create-table
  "Create a RethinkDB table with the specified primary key if it doesn't exist."
  [conn db-name table-name primary-key]
  (when (not-any? #(= table-name %) (table-list conn db-name))
    (-> (r/db db-name)
      (r/table-create table-name {:primary-key primary-key :durability "hard"})
      (r/run conn))))

(defn- index-list
  "Return a sequence of the index names for a table in the RethinkDB."
  [conn table-name]
  (-> (r/table table-name) (r/index-list) (r/run conn)))

(defn- wait-for-index
  "Pause until an index with the specified name is finished being created."
  [conn table-name index-name]
  (-> (r/table table-name)
    (r/index-wait index-name)
    (r/run conn)))

(defn- create-index
  "Create RethinkDB table index for the specified field if it doesn't exist."
  ([conn table-name index-name]
    (when (not-any? #(= index-name %) (index-list conn table-name))
      (-> (r/table table-name)
        (r/index-create index-name)
        (r/run conn))
      (wait-for-index conn table-name index-name))))

(defn- create-sections-indexes
  "Create RethinkDB table indexes for the sections table if they doesn't exist."
  [conn]
    (let [indexes (index-list conn section/table-name)]
      (when (not-any? #(= "company-slug-section-name" %) indexes)
        (-> (r/table section/table-name)
          (r/index-create "company-slug-section-name"
            (r/fn [row] [(r/get-field row :company-slug) (r/get-field row :section-name)]))
          (r/run conn))
        (wait-for-index conn section/table-name "company-slug-section-name"))
      (when (not-any? #(= "company-slug-section-name-updated-at" %) indexes)
        (-> (r/table section/table-name)
          (r/index-create "company-slug-section-name-updated-at"
            (r/fn [row] [
              (r/get-field row :company-slug)
              (r/get-field row :section-name)
              (r/get-field row :updated-at)]))
          (r/run conn))
        (wait-for-index conn section/table-name "company-slug-section-name-updated-at"))))

(defn init
  "Create any missing tables and indexes in RethinkDB."
  []
  (let [db-name c/db-name]
    (println (str "\nOpen Company: Initializing database - " db-name))
    (with-open [conn (apply r/connect c/db-options)]
      (when (create-database conn db-name)
        (create-table conn db-name company/table-name company/primary-key)
        (print ".")
        (create-table conn db-name section/table-name section/primary-key)
        (print ".")
        (create-index conn section/table-name "company-slug")
        (print ".")
        (create-sections-indexes conn)
        (print ".")
        (println "\nOpen Company: Database initialization complete - " db-name "\n")))))

(defn -main
  "Initialize the RethinkDB instance."
  []
  (init))