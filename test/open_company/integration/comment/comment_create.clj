(ns open-company.integration.comment.comment-create
  (:require [midje.sweet :refer :all]
            [open-company.lib.rest-api-mock :as mock]
            [open-company.lib.hateoas :as hateoas]
            [open-company.lib.resources :as r]
            [open-company.lib.db :as db]
            [open-company.resources.company :as company]
            [open-company.resources.section :as section]
            [open-company.resources.comment :as comment]
            [open-company.representations.comment :as comment-rep]))

;; ----- Startup -----

(db/test-startup)

;; ----- Test Cases -----

;; Creating comments with the REST API

;; The system should store newly created valid comments and handle the following scenarios:

;; POST
;; all good - 201 Created
;; all good, unicode in the body - 201 Created

;; no accept
;; no content type
;; no charset
;; conflicting reserved properties
;; wrong accept
;; wrong content type
;; no body
;; wrong charset
;; body not valid JSON
;; no body in body

;; ----- Tests -----