(ns open-company.representations.comment
  (:require [open-company.representations.common :as common]))
  
(def media-type "application/vnd.open-company.comment.v1+json")

(defn url
  ([company-slug section-name]
  (str "/companies/" (name company-slug) "/" (name section-name) "/comments"))
  ([company-slug section-name comment-id]
  (str (url company-slug section-name) "/" comment-id)))

(defn- response-link
  "Add a :links key to the comment with a HATEAOS link to leave a response."
  [company-slug section-name comment]
  (if (:response-to comment)
    (assoc comment :links []) ; no response link for a response
    (assoc comment :links [
      (common/link-map "respond" common/POST (str (url company-slug section-name (:comment-id comment))) media-type)])))

(defn comment-link
  "Return a link to create new comments on a section."
  [company-slug section-name]
  (common/link-map "comment" common/POST (url company-slug section-name) media-type))

(defn comment-links
  "Add the HATEAOS links to the section's comments."
  ([section] (comment-links (:company-slug section) (:section-name section) section))
  
  ([company-slug section-name section]
  (assoc section :comments (map #(response-link company-slug section-name %) (:comments section)))))