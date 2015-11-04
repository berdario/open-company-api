(ns open-company.unit.resources.comment.comment-creation
  (:require [midje.sweet :refer :all]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [open-company.lib.check :as check]
            [open-company.lib.resources :as r]
            [open-company.lib.db :as db]
            [open-company.resources.company :as c]
            [open-company.resources.section :as s]
            [open-company.db.pool :as pool]))

;; ----- Startup -----

(db/test-startup)

;; ----- Utility functions -----


;; ----- Tests -----

(with-state-changes [(before :facts (c/delete-all-companies!))
                     (after :facts (c/delete-all-companies!))]

  (facts "about initial empty comments"

    (fact "when a company is created"
      ;; on return from the create function
      (get-in (c/create-company (assoc r/open :finances r/finances-section-1) r/coyote) [:finances :comments]) => []
      ;; on company retrieval from the DB
      (get-in (c/get-company r/slug) [:finances :comments]) => []
      ;; on section retrieval from the DB
      (:comments (s/get-section r/slug "finances")) => [])

    (with-state-changes [(before :facts (c/create-company r/open r/coyote))]

      (fact "when a section is added"  
        ;; on return from the create function
        (:comments (s/put-section r/slug "finances" r/finances-section-1 r/coyote)) => []
        ;; on section retrieval from the DB
        (:comments (s/get-section r/slug "finances")) => [])

      (fact "when a section is revised"
        (s/put-section r/slug "finances" r/finances-section-1 r/coyote)
        ;; on return from the create function
        (:comments (s/put-section r/slug "finances" r/finances-section-2 r/camus)) => []
        ;; on section retrieval from the DB
        (:comments (s/get-section r/slug "finances")) => [])))

  (future-facts "about creating initial comments")

  (future-facts "about creating subsequent comments")

  (future-facts "about creating comment responses"))