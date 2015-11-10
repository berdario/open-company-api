(ns open-company.representations.comment
  (:require [clojure.string :as s]
            [open-company.representations.common :as common]
            [defun :refer (defun-)]))
  
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

(defun- response-map
  "Create a map by comment-id of a sorted array of all the responses to that "

  ;; initial
  ([comments] (response-map comments {}))

  ;; all done
  ([comments :guard empty? responses] responses)

  ([comments responses]
  (let [this-comment (first comments)
        response-id (:response-to this-comment)
        comment-responses (responses response-id) ; sibling responses, if any
        new-responses (if comment-responses
                        (assoc responses response-id (conj comment-responses this-comment)) ; this is another response
                        (assoc responses response-id [this-comment]))] ; this is the first response
    (recur (rest comments) new-responses))))

(defun- collapse-comments-responses
  "Given a sorted set of comments, add any responses to them, in reverse chronological order after the
  comment and before the next comment"
  
  ;; initial
  ([comments response-map] (collapse-comments-responses comments response-map []))
  
  ;; all done
  ([comments :guard empty? response-map final-comments] (vec (flatten final-comments)))

  ([comments response-map accumulate-comments]
    (let [this-comment (first comments)
          comment-id (:comment-id this-comment)
          final-comments (conj accumulate-comments this-comment) ; comments so far plus this new comment
          responses (response-map comment-id)] ; responses, if any
      ;; recur, adding in responses to this comment if there are any
      (recur (rest comments) response-map (if responses (conj final-comments responses) final-comments)))))

(defn comment-order
  "
  Put comments into a natural order for display:

  - Reverse chronological
  - Replies following the comment they reply to.
  "
  [section]
  (let [all-comments (:comments section)
        comments (reverse (sort-by :updated-at (remove :response-to all-comments))) ; all comments in reverse chrono
        responses (sort-by :updated-at (filter :response-to all-comments)) ; all responses in chrono order
        response-map (response-map responses)
        final-comments (collapse-comments-responses comments response-map)]
    (assoc section :comments final-comments)))