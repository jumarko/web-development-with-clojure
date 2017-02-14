;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns picture-gallery.routes.services
  (:require [picture-gallery.routes.services.auth :as auth]
            [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]))

(s/defschema UserRegistration
  {:id           String
   :pass         String
   :pass-confirm String})

(s/defschema Result
  {:result                   s/Keyword
   (s/optional-key :message) String})

(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "Picture Gallery API"
                           :description "Public Services"}}}}
  (POST "/register" req
        :return Result
        :body [user UserRegistration]
        :summary "register a new user"
        (auth/register! req user))
  (POST "/login" req
        :header-params [authorization :- String]
        :summary "log in the user and create a session"
        :return Result
        (auth/login! req authorization))
  (POST "/logout" []
        :summary "remove user session"
        :return Result
        (auth/logout!))
  )
