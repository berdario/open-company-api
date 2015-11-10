(ns open-company.unit.representations.comment
  (:require [midje.sweet :refer :all]
            [open-company.representations.comment :as comment-rep]))

(facts "about section comment links"

  (fact "comment link for a section is valid"
    (comment-rep/comment-link "buffer" "press") => {
      :href "/companies/buffer/press/comments"
      :method "POST"
      :rel "comment"
      :type comment-rep/media-type
    }))

(facts "about comment response links"

  (fact "comments get response links"

    (comment-rep/comment-links {
      :company-slug "buffer"
      :section-name "press"
      :comments [{:comment-id "123-abc"}]
      }) => {
      :company-slug "buffer"
      :section-name "press"
      :comments [{:comment-id "123-abc" :links [{
          :href "/companies/buffer/press/comments/123-abc"
          :method "POST"
          :rel "respond"
          :type comment-rep/media-type        
        }]}]
      })

  (fact "responses don't get response links"

    (comment-rep/comment-links {
      :company-slug "buffer"
      :section-name "press"
      :comments [{:comment-id "456-def" :response-to "123-abc"}]
      }) => {
      :company-slug "buffer"
      :section-name "press"
      :comments [{:comment-id "456-def" :response-to "123-abc" :links []}]})

  (fact "multiple comments get the right response links"
    (comment-rep/comment-links {
      :company-slug "buffer"
      :section-name "press"
      :comments [{:comment-id "123-abc"} {:comment-id "456-def" :response-to "123-abc"}]
      }) => {
      :company-slug "buffer"
      :section-name "press"
      :comments [
        {
          :comment-id "123-abc" :links [{
            :href "/companies/buffer/press/comments/123-abc"
            :method "POST"
            :rel "respond"
            :type comment-rep/media-type        
          }]
        }
        {
          :comment-id "456-def"
          :response-to "123-abc"
          :links []
        }
      ]}))

(facts "about comment order"

  (facts "comments are in reverse chronological order"
    (comment-rep/comment-order {
        :comments [
          {:comment-id "later" :updated-at "2015-06-02T12:00:00.000Z"}
          {:comment-id "earlier" :updated-at "2015-06-01T12:00:00.000Z"}]}) =>
        {:comments [
          {:comment-id "later" :updated-at "2015-06-02T12:00:00.000Z"}
          {:comment-id "earlier" :updated-at "2015-06-01T12:00:00.000Z"}]}
    (comment-rep/comment-order {
        :comments [
          {:comment-id "earlier" :updated-at "2015-06-01T12:00:00.000Z"}
          {:comment-id "later" :updated-at "2015-06-02T12:00:00.000Z"}]}) =>
        {:comments [
          {:comment-id "later" :updated-at "2015-06-02T12:00:00.000Z"}
          {:comment-id "earlier" :updated-at "2015-06-01T12:00:00.000Z"}]})

  (fact "comment responses are after the comment they respond to, in reverse chronological order"
    (comment-rep/comment-order {
        :comments [
          {:comment-id "earlier" :updated-at "2015-06-01T12:00:00.000Z"}
          {:comment-id "later" :updated-at "2015-06-02T12:00:00.000Z"}
          {:comment-id "earlier-response" :updated-at "2015-06-03T12:00:00.000Z" :response-to "earlier"}
          {:comment-id "later-response" :updated-at "2015-06-04T12:00:00.000Z" :response-to "later"}
          {:comment-id "earlier-response-later" :updated-at "2015-06-05T12:00:00.000Z" :response-to "earlier"}]}) =>
        {:comments [
          {:comment-id "later" :updated-at "2015-06-02T12:00:00.000Z"}
          {:comment-id "later-response" :updated-at "2015-06-04T12:00:00.000Z" :response-to "later"}
          {:comment-id "earlier" :updated-at "2015-06-01T12:00:00.000Z"}
          {:comment-id "earlier-response-later" :updated-at "2015-06-05T12:00:00.000Z" :response-to "earlier"}
          {:comment-id "earlier-response" :updated-at "2015-06-03T12:00:00.000Z" :response-to "earlier"}]}))