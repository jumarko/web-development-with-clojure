;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns picture-gallery.ajax
  (:require [ajax.core :as ajax]
            [reagent.session :as session]))

(defn default-headers [request]
  (-> request
      (update :uri #(str js/context %))
      (update
        :headers
        #(merge
           %
           {"Accept" "application/transit+json"
            "x-csrf-token" js/csrfToken}))))

(defn user-action [request]
  (session/put! :user-event true)
  request)

(defn load-interceptors! []
  (swap! ajax/default-interceptors
         into
         [(ajax/to-interceptor {:name "default headers"
                                :request default-headers})
          (ajax/to-interceptor {:name "user action"
                                :request user-action})]))

