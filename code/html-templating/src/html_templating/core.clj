;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns html-templating.core
  (:require [selmer.parser :as selmer]
            [selmer.filters :as filters]
            [selmer.middleware :refer [wrap-error-page]]))

(selmer.parser/cache-off!)

(selmer/render "Hello, {{name}}" {:name "World"})

(selmer/render-file "hello.html" {:items (range 10)})

(filters/add-filter! :empty? empty?)

(selmer/render "{% if files|empty? %}no files{% else %}files{% endif %}"
  {:files []})

(filters/add-filter! :foo
  (fn [x] [:safe (.toUpperCase x)]))

(selmer/render "{{x|foo}}" {:x "<div>I'm safe</div>"})

(selmer/add-tag!
 :image
 (fn [args context-map]
    (str "<img src=" (first args) "/>")))

(selmer/render "{% image \"http://foo.com/logo.jpg\" %}" {})

(selmer/add-tag!
 :uppercase
 (fn [args context-map content]
   (.toUpperCase (get-in content [:uppercase :content])))
 :enduppercase)

(selmer/render "{% uppercase %}foo {{bar}} baz{% enduppercase %}" {:bar "injected"})

((wrap-error-page
  (fn [_] (selmer/render-file "error.html" {}))) {})
