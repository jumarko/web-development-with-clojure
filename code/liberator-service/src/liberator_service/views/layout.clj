;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns liberator-service.views.layout
  (:require [hiccup.page :refer [html5 include-css]]))
       
(defn common [& body]  
  (html5
    [:head
     [:title "Welcome to liberator-service"]
     (include-css "/css/screen.css")]
    [:body body]))
