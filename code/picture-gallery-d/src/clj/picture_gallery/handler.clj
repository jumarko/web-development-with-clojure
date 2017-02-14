;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns picture-gallery.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [picture-gallery.layout :refer [error-page]]
            [picture-gallery.routes.home :refer [home-routes]]
            [picture-gallery.routes.services
             :refer [service-routes restricted-service-routes]]
            [compojure.route :as route]
            [picture-gallery.middleware :as middleware]))

(def app-routes
  (routes
    #'service-routes
    (wrap-routes #'restricted-service-routes middleware/wrap-auth)
    (wrap-routes #'home-routes middleware/wrap-csrf)
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))

(def app (middleware/wrap-base #'app-routes))
