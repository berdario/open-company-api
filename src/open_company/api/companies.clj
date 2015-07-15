(ns open-company.api.companies
  (:require [compojure.core :refer (defroutes ANY)]
            [liberator.core :refer (defresource by-method)]
            [open-company.api.common :as common]
            [open-company.resources.company :as company]
            [open-company.representations.company :as render]))

;; ----- Responses -----

(defn- company-location-response [company]
  (common/location-response ["v1" "companies" (:symbol company)] (render/render-company company) company/media-type))

(defn- unprocessable-reason [reason]
  (case reason
    :bad-company common/missing-response
    :no-name (common/unprocessable-entity-response "Company name is required.")
    :symbol-conflict (common/unprocessable-entity-response "Ticker symbol is already used.")
    :invalid-symbol (common/unprocessable-entity-response "Invalid ticker symbol.")
    (common/unprocessable-entity-response "Not processable.")))

;; ----- Actions -----

(defn- get-company [ticker]
  (if-let [company (company/get-company ticker)]
    {:company company}))

(defn- put-company [ticker company]
  (let [full-company (assoc company :symbol ticker)]
    (when (company/put-company ticker full-company)
      {:company full-company})))

;; ----- Resources - see: http://clojure-liberator.github.io/liberator/assets/img/decision-graph.svg

(defresource company [ticker]
  common/open-company-resource

  :available-media-types [company/media-type]
  :exists? (fn [_] (get-company ticker))
  :known-content-type? (fn [ctx] (common/known-content-type? ctx company/media-type))
  
  :processable? (by-method {
    :get true
    :put (fn [ctx] (common/check-input (company/valid-company ticker (:data ctx))))})

  ;; Handlers
  :handle-ok (by-method {
    :get (fn [ctx] (render/render-company (:company ctx)))
    :put (fn [ctx] (render/render-company (:company ctx)))})
  :handle-not-acceptable (fn [_] (common/only-accept 406 company/media-type))
  :handle-unsupported-media-type (fn [_] (common/only-accept 415 company/media-type))
  :handle-unprocessable-entity (fn [ctx] (unprocessable-reason (:reason ctx)))

  ;; Delete a company
  :delete! (fn [_] (company/delete-company ticker))

  ;; Create or update a company
  :new? (by-method {:put (not (company/get-company ticker))})
  :put! (fn [ctx] (put-company ticker (:data ctx)))
  :handle-created (fn [ctx] (company-location-response (:company ctx))))

;; ----- Routes -----

(defroutes company-routes
  (ANY "/v1/companies/:ticker" [ticker] (company ticker)))