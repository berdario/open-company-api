(ns open-company.resources.comment
  (:require [clojure.string :as s]
            [open-company.resources.common :as common]
            [open-company.resources.company :as company]
            [open-company.resources.section :as section]))

(defn- uuid
  "Simple wrapper for Java's UUID"
  []
  (str (java.util.UUID/randomUUID)))

(defn- short-uuid []
  "
  Take the middle 3 sections of a Java UUID to make a shorter UUID.

  Ex: f6f7-499f-b805
  "
  (s/join "-" (take 3 (rest (s/split (uuid) #"-")))))

(defn create-comment 
  "
  Given a company slug, section name, comment map and the comment's author, update the section and company with
  the new comment.

  TODO: make sure if there is a response-to, it's to an existing comment.
  "
  [company-slug section-name comment author]
  (let [company (company/get-company company-slug)
        section (section/get-section company-slug section-name)
        comment-timestamp (common/current-timestamp)
        ;; get the comment ready
        final-comment (-> comment
                        (assoc :comment-id (short-uuid))
                        (assoc :author author)
                        (assoc :created-at comment-timestamp)
                        (assoc :updated-at comment-timestamp))
        ;; add the comment to the section
        final-section (assoc section :comments (conj (:comments section) final-comment))]
    (if (and company section)
      (do
        ;; store the section w/ the comment
        (common/update-resource section/table-name section/primary-key section final-section (:updated-at section))
        ;; store the company w/ the section 
        (common/update-resource company/table-name company/primary-key company
          (assoc company (keyword section-name) final-section) (:updated-at company))
        final-comment)
      false)))