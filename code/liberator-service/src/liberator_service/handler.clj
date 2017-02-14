;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns liberator-service.handler
  (:require [compojure.core :refer [defroutes routes]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [liberator-service.routes.home :refer [home-routes]]))

(defn init []
  (println "liberator-service is starting"))

(defn destroy []
  (println "liberator-service is shutting down"))
  
(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (-> (routes home-routes app-routes) handler/site))
