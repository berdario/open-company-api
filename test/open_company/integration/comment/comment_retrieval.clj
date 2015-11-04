(ns open-company.integration.comment.comment-retrieval
  (:require [clojure.string :as s]
            [midje.sweet :refer :all]
            [open-company.lib.rest-api-mock :as mock]
            [open-company.lib.hateoas :as hateoas]
            [open-company.lib.check :as check]
            [open-company.lib.resources :as r]
            [open-company.lib.db :as db]
            [open-company.resources.company :as company]
            [open-company.resources.section :as section]
            [open-company.resources.comment :as comment]
            [open-company.representations.common :as common-rep]
            [open-company.representations.company :as company-rep]
            [open-company.representations.section :as section-rep]))

;; ----- Startup -----

(db/test-startup)

;; ----- Test Cases -----

;; Retrieving comments with the REST API

;; The system should return comments with the company and with sections, and handle the following scenarios:

;; no comments
;; 1 comment
;; comment and reply
;; many comments

;; ----- Tests -----

(with-state-changes [(before :facts (do (company/delete-all-companies!)
                                        (company/create-company r/open-with-finances r/coyote)))
                     (after :facts (company/delete-all-companies!))]

  (facts "about comment retrieval"

    (facts "when there are no comments"

      (facts "when GETing a company and a section"

        (let [company-response (mock/api-request (company-rep/url r/slug))
              company-body (:finances (mock/body-from-response company-response))
              section-response (mock/api-request (section-rep/url r/slug "finances"))
              section-body (mock/body-from-response section-response)]
          (:status company-response) => 200
          (:status section-response) => 200

          (doseq [body [company-body section-body]]

            (fact "there is an empty comments array"
              (:comments body) => [])

            (fact "there is a comment creation link"
              (hateoas/verify-section-links r/slug "finances" (:links body)))))))

    (with-state-changes [(before :facts (comment/create-comment r/slug "finances" r/comment-1 r/camus))]

      (facts "when there is 1 comment"

        (facts "when GETing a company and a section"

          (let [company-response (mock/api-request (company-rep/url r/slug))
                company-body (:finances (mock/body-from-response company-response))
                section-response (mock/api-request (section-rep/url r/slug "finances"))
                section-body (mock/body-from-response section-response)]
            (:status company-response) => 200
            (:status section-response) => 200

            (doseq [body [company-body section-body]]

              (fact "the comment is in the comments array"
                (let [comments (:comments body)
                      comment-1 (first comments)]
                  (count comments) => 1
                  (:author comment-1) => r/camus
                  (:body comment-1) => (:body r/comment-1)
                  (:response-to comment-1) => nil
                  (s/blank? (:comment-id comment-1)) => false
                  (string? (:comment-id comment-1)) => true
                  (check/timestamp? (:updated-at comment-1)) => true
                  (check/about-now? (:updated-at comment-1)) => true
                  (:updated-at comment-1) => (:created-at comment-1)))

              (fact "there is a comment creation link"
                (hateoas/verify-section-links r/slug "finances" (:links body)))))))

      (with-state-changes [(before :facts
        (let [comment-id (:comment-id (last (:comments (section/get-section r/slug "finances"))))]
          (comment/create-comment r/slug "finances" (assoc r/comment-2 :response-to comment-id) r/coyote)))]

        (facts "when there is a comment and a reply"

          (facts "when GETing a company and a section"

            (let [company-response (mock/api-request (company-rep/url r/slug))
                  company-body (:finances (mock/body-from-response company-response))
                  section-response (mock/api-request (section-rep/url r/slug "finances"))
                  section-body (mock/body-from-response section-response)]
              (:status company-response) => 200
              (:status section-response) => 200

              (doseq [body [company-body section-body]]

                (fact "the original comment is in the comments array"
                  (let [comments (:comments body)
                        comment-1 (first comments)]
                    (count comments) => 2
                    (:author comment-1) => r/camus
                    (:body comment-1) => (:body r/comment-1)
                    (:response-to comment-1) => nil
                    (s/blank? (:comment-id comment-1)) => false
                    (string? (:comment-id comment-1)) => true
                    (check/timestamp? (:updated-at comment-1)) => true
                    (check/about-now? (:updated-at comment-1)) => true
                    (:updated-at comment-1) => (:created-at comment-1)))

                (fact "the response comment is in the comments array"
                  (let [comments (:comments body)
                        comment-2 (last comments)]
                    (count comments) => 2
                    (:author comment-2) => r/coyote
                    (:body comment-2) => (:body r/comment-2)
                    (:response-to comment-2) => (:comment-id (first comments))
                    (s/blank? (:comment-id comment-2)) => false
                    (string? (:comment-id comment-2)) => true
                    (check/timestamp? (:updated-at comment-2)) => true
                    (check/about-now? (:updated-at comment-2)) => true
                    (:updated-at comment-2) => (:created-at comment-2)))

                (fact "there is a comment creation link"
                  (hateoas/verify-section-links r/slug "finances" (:links body)))))))

        (with-state-changes [(before :facts (comment/create-comment r/slug "finances" r/comment-3 r/camus))]

          (facts "when there are many comments"

            (facts "when GETing a company and a section"

              (let [company-response (mock/api-request (company-rep/url r/slug))
                    company-body (:finances (mock/body-from-response company-response))
                    section-response (mock/api-request (section-rep/url r/slug "finances"))
                    section-body (mock/body-from-response section-response)]
                (:status company-response) => 200
                (:status section-response) => 200

                (doseq [body [company-body section-body]]

                  (fact "the comments are in the comments array"
                    (let [comments (:comments body)]
                      (count comments) => 3
                      (map :author comments) => [r/camus r/coyote r/camus]
                      (map :body comments) => [(:body r/comment-1) (:body r/comment-2) (:body r/comment-3)]
                      (map :response-to comments) => [nil (:comment-id (first comments)) nil]
                      (map s/blank? (map :comment-id comments)) => [false false false]
                      (map string? (map :comment-id comments)) => [true true true]
                      (map check/timestamp? (map :updated-at comments)) => [true true true]
                      (map check/about-now? (map :updated-at comments)) => [true true true]
                      (map :updated-at comments) => (map :created-at comments)))

                  (fact "there is a comment creation link"
                    (hateoas/verify-section-links r/slug "finances" (:links body))))))))))))