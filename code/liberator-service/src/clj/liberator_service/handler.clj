;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns liberator-service.handler
  (:require
            [compojure.core :refer [ANY defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]
            [environ.core :refer [env]]
            [clojure.java.io :as io]
            [liberator.core :refer [defresource resource]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defn home-page []
  (html
   [:html
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1"}]
     (include-css (if (env :dev) "css/site.css" "css/site.min.css"))]
    [:body
     (anti-forgery-field)
     [:p (str (anti-forgery-field))]
     [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]]
     (include-js "js/app.js")]]))

(defresource home
  :allowed-methods [:get]
  :handle-ok (home-page)
  :etag "fixed-etag"
  :available-media-types ["text/html"])

(defresource get-items
  :allowed-methods [:get]
  :handle-ok (fn [_] (io/file "items"))
  :available-media-types ["text/plain"])

(defresource add-item!
  :allowed-methods [:post]
  :malformed? (fn [context]
                (-> context :request :params :item empty?))
  :handle-malformed "item value cannot be empty!"
  :post!
  (fn [context]
    (let [item (-> context :request :params :item)]
      (spit (io/file "items") (str item "\n") :append true)))
  :handle-created "ok"
  :available-media-types ["text/plain"])

(defroutes routes
  (ANY "/" request home)
  (ANY "/items" request get-items)
  (ANY "/add-item" request add-item!))

(def app
  (let [handler (wrap-defaults #'routes site-defaults)]
    (if (env :dev) (-> handler wrap-exceptions wrap-reload) handler)))
