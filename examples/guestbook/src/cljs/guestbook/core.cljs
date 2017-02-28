(ns guestbook.core
  (:require [reagent.core :as r :refer [atom]]))

(defn home
  []
  [:div#hello.content>p "Hello world!"])

(r/render
 [home]
 (.getElementById js/document "content"))
