(ns open-company.representations.comment)

(def media-type "application/vnd.open-company.comment.v1+json")

(defn url
  ([company-slug section-name]
  (str "/companies/" (name company-slug) "/" (name section-name) "/comments"))
  ([company-slug section-name comment-id]
  (str (url company-slug section-name) "/" comment-id)))