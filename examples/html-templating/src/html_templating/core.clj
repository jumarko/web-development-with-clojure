(ns html-templating.core
  (:require [selmer.parser :as s]
            [selmer.filters :as f]))

(f/add-filter! :empty? empty?)

(s/render "{% if files|empty? %}no files{% else %}files{% endif %}" {:files []})

;; by default the content of the filters will be escaped -> we can override this:
(f/add-filter! :foo
               (fn [x] [:safe (.toUpperCase x)]))
(s/render "{{x|foo}}" {:x "<div>I'm safe</div>"})


;;; Using tags

;; define custom tags
(s/add-tag!
 :image
 (fn [args context-map]
   (str "<img src=" (first args) "/>")))

(s/render "{% image \"http://foo.com/logo.jpg\" %}" {})

;; we can also define block tags
(s/add-tag!
 :uppercase
 (fn [args context-map content]
   (.toUpperCase (get-in content [:uppercase :content])))
 :enduppercase)

 (s/render "{% uppercase %}foo {{bar}} baz{% enduppercase %}" {:bar "injected"})
 
