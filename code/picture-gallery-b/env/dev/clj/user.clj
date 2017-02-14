;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns user
  (:require [mount.core :as mount]
            picture-gallery.core))

(defn start []
  (mount/start-without #'picture-gallery.core/repl-server))

(defn stop []
  (mount/stop-except #'picture-gallery.core/repl-server))

(defn restart []
  (stop)
  (start))


