(ns open-company.unit.resources.comment.comment-creation
  (:require [midje.sweet :refer :all]
            [clojure.string :as s]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [open-company.lib.check :as check]
            [open-company.lib.resources :as r]
            [open-company.lib.db :as db]
            [open-company.resources.company :as company]
            [open-company.resources.section :as section]
            [open-company.resources.comment :as comment]))

;; ----- Startup -----

(db/test-startup)

;; ----- Tests -----

;; TODO tests for comment creation failures

(with-state-changes [(before :facts (company/delete-all-companies!))
                     (after :facts (company/delete-all-companies!))]

  (facts "about initial empty comments"

    (fact "when a company is created"
      ;; on return from the create function
      (get-in (company/create-company r/open-with-finances r/coyote) [:finances :comments]) => []
      ;; on company retrieval from the DB
      (get-in (company/get-company r/slug) [:finances :comments]) => []
      ;; on section retrieval from the DB
      (:comments (section/get-section r/slug "finances")) => [])

    (with-state-changes [(before :facts (company/create-company r/open r/coyote))]

      (fact "when a section is added"
        ;; on return from the create function
        (:comments (section/put-section r/slug "finances" r/finances-section-1 r/coyote)) => []
        ;; on section retrieval from the DB
        (:comments (section/get-section r/slug "finances")) => [])

      (fact "when a section is revised"
        (section/put-section r/slug "finances" r/finances-section-1 r/coyote)
        ;; on return from the create function
        (:comments (section/put-section r/slug "finances" r/finances-section-2 r/camus)) => []
        ;; on section retrieval from the DB
        (:comments (section/get-section r/slug "finances")) => [])))

  (with-state-changes [(before :facts (company/create-company r/open-with-finances r/coyote))]

    (facts "about creating initial comments"
      (let [comment-return (comment/create-comment r/slug "finances" r/comment-1 r/camus)
            company (company/get-company r/slug)
            section (section/get-section r/slug "finances")
            comment-retrieve-1 (first (:comments (:finances company)))
            comment-retrieve-2 (first (:comments section))]
        (doseq [comment-1 [comment-return comment-retrieve-1 comment-retrieve-2]]
          (:author comment-1) => r/camus
          (:body comment-1) => (:body r/comment-1)
          (:response-to comment-1) => nil
          (s/blank? (:comment-id comment-1)) => false
          (string? (:comment-id comment-1)) => true
          (check/timestamp? (:updated-at comment-1)) => true
          (check/about-now? (:updated-at comment-1)) => true
          (:updated-at comment-1) => (:created-at comment-1))
        (count (:comments (:finances company))) => 1
        (count (:comments section)) => 1))

    (with-state-changes [(before :facts (comment/create-comment r/slug "finances" r/comment-1 r/camus))]

      (facts "about creating subsequent comments"
        (let [comment-return (comment/create-comment r/slug "finances" r/comment-2 r/coyote)
              company (company/get-company r/slug)
              section (section/get-section r/slug "finances")
              comment-retrieve-1 (last (:comments (:finances company)))
              comment-retrieve-2 (last (:comments section))]
          (doseq [comment-2 [comment-return comment-retrieve-1 comment-retrieve-2]]
            (:author comment-2) => r/coyote
            (:body comment-2) => (:body r/comment-2)
            (:response-to comment-2) => nil
            (s/blank? (:comment-id comment-2)) => false
            (string? (:comment-id comment-2)) => true
            (check/timestamp? (:updated-at comment-2)) => true
            (check/about-now? (:updated-at comment-2)) => true
            (:updated-at comment-2) => (:created-at comment-2))
          (count (:comments (:finances company))) => 2
          (count (:comments section)) => 2))

      (facts "about creating comment responses"
        (let [comment-id (:comment-id (last (:comments (section/get-section r/slug "finances"))))
              response (assoc r/comment-2 :response-to comment-id)
              comment-return (comment/create-comment r/slug "finances" response r/coyote)
              company (company/get-company r/slug)
              section (section/get-section r/slug "finances")
              comment-retrieve-1 (last (:comments (:finances company)))
              comment-retrieve-2 (last (:comments section))]
          (doseq [comment-2 [comment-return comment-retrieve-1 comment-retrieve-2]]
            (:author comment-2) => r/coyote
            (:body comment-2) => (:body r/comment-2)
            (:response-to comment-2) => comment-id
            (s/blank? (:comment-id comment-2)) => false
            (string? (:comment-id comment-2)) => true
            (check/timestamp? (:updated-at comment-2)) => true
            (check/about-now? (:updated-at comment-2)) => true
            (:updated-at comment-2) => (:created-at comment-2))
          (count (:comments (:finances company))) => 2
          (count (:comments section)) => 2)))))