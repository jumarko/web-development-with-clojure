;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns leiningen.new.compojure
  (:require [leiningen.core.main :as main]
            [leiningen.new.templates :refer [renderer year project-name
                                             ->files sanitize-ns name-to-path
                                             multi-segment]]))

(def render (renderer "compojure"))

(defn compojure
  "Create a new Compojure project"
  [name]
  (let [main-ns (sanitize-ns name)
        data    {:raw-name    name
                 :name        (project-name name)
                 :namespace   main-ns
                 :dirs        (name-to-path main-ns)
                 :year        (year)}]
    (->files data
             [".gitignore"  (render "gitignore")]
             ["project.clj" (render "project.clj" data)]
             ["README.md"   (render "README.md" data)]
             ["src/{{dirs}}/handler.clj"       (render "handler.clj" data)]
             ["test/{{dirs}}/handler_test.clj" (render "handler_test.clj" data)]
             "resources/public")))
