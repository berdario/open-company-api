(ns open-company.lib.hateoas
  "Utility functions for testing HATEOAS https://en.wikipedia.org/wiki/HATEOAS"
  (:require [open-company.lib.check :refer (check)]
            [open-company.representations.common :refer (GET POST PUT PATCH DELETE)]
            [open-company.resources.company :as company]))

(defn- find-link [rel links]
  (some (fn [link] (if (= rel (:rel link)) link nil)) links))

(defn verify-link [rel method href type links]
  (if-let [link (find-link rel links)]
    (do
      (check (= method (:method link)))
      (check (= href (:href link)))
      (if (= :no type)
        (check (nil? (:type link)))
        (check (= type (:type link)))))
    (check (= rel :link_not_present))))

(defn verify-company-links [ticker links]
  (check (= (count links) 4))
  (let [url (str "/v1/companies/" ticker)]
    (verify-link "self" GET url company/media-type links)
    (verify-link "update" PUT url company/media-type links)
    (verify-link "partial-update" PATCH url company/media-type links)
    (verify-link "delete" DELETE url :no links)))