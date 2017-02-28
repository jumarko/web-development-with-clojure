(ns guestbook.core
  (:require [reagent.core :as r :refer [atom]]))

;; Let's reimplement the form in Reagent
(defn message-form []
  (let [fields (atom {})]
    (fn []
      [:div.content
       [:div.form-group
        ;; helper element to see the actual values in atom
        [:p "name:" (:name @fields)
         [:p "message:" (:message @fields)]]
        [:p "Name:"
         [:input.form-control
          {:type :text
           :name :name
           :on-change #(swap! fields assoc :name (-> % .-target .-value))
           :value (:name @fields)}]]]
       [:p "Message:"
        [:textarea.form-control
         {:rows 4
          :cols 4
          :name :message
          :on-change #(swap! fields assoc :message (-> % .-target .-value))}
         (:message @fields)]]
       [:input.btn.btn-primary {:type :submit :value "comment"}]])))

(defn home []
  [:div row
   [:div.span12
    [message-form]]])

(r/render
 [home]
 (.getElementById js/document "content"))
