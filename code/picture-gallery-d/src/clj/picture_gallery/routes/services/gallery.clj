;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns picture-gallery.routes.services.gallery
  (:require [picture-gallery.layout :refer [error-page]]
            [picture-gallery.db.core :as db]
            [ring.util.http-response :refer :all])
  (:import java.io.ByteArrayInputStream))

(defn get-image [owner name]
  (if-let [{:keys [type data]} (db/get-image {:owner owner :name name})]
    (-> (ByteArrayInputStream. data)
        (ok)
        (content-type type))
    (error-page {:status 404
                 :title "page not found"})))

(defn list-thumbnails [owner]
  (ok (db/list-thumbnails {:owner owner})))

(defn list-galleries []
  (ok (db/select-gallery-previews)))
