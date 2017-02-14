;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
<code file="code/liberator-snippets/home.clj" part="home-resource"/>
(ns liberator-service.routes.home
  (:require [compojure.core :refer :all]
            [liberator.core
             :refer [defresource resource request-method-in]]))

(defroutes routes
   (ANY "/" request
     (resource
       :handle-ok home-page
       :etag "fixed-etag"
       :available-media-types ["text/html"])))

(defresource home
  :handle-ok home-page
  :etag "fixed-etag"
  :available-media-types ["text/html"])

(defroutes routes
  (ANY "/" request home))

(defresource home
  :service-available? false
  :handle-ok home-page
  :etag "fixed-etag"
  :available-media-types ["text/html"])

(defresource home
  :method-allowed?
  (fn [context]
    (= :get (get-in context [:request :request-method])))
  :handle-ok home-page
  :etag "fixed-etag"
  :available-media-types ["text/html"])

(defresource home
  :allowed-methods [:get]
  :handle-ok home-page
  :etag "fixed-etag"
  :available-media-types ["text/html"])

(defresource home
  :service-available? true

  :method-allowed? (request-method-in :get)

  :handle-method-not-allowed
  (fn [context]
    (str (get-in context [:request :request-method]) " is not allowed"))

  :handle-ok home-page
  :etag "fixed-etag"
  :available-media-types ["text/html"])

(defresource home
  :service-available? false
  :handle-service-not-available
  "service is currently unavailable..."

  :method-allowed? (request-method-in :get)
  :handle-method-not-allowed
  (fn [context]
    (str (get-in context [:request :request-method]) " is not allowed"))

  :handle-ok home-page
  :etag "fixed-etag"
  :available-media-types ["text/html"])


(defresource add-item
  :method-allowed? (request-method-in :post)
  :post!
  (fn [context]
    (let [item (-> context :request :params :item)]
      (spit (io/file "items") (str item "\n") :append true)))
  :handle-created (io/file "items")
  :available-media-types ["text/plain"])




