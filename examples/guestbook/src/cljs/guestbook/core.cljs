(ns guestbook.core
  (:require [reagent.core :as r :refer [atom]]
            [ajax.core :refer [GET POST]]))

;; Let's reimplement the form in Reagent

(defn- get-messages [messages]
  (GET "/messages"
    {:headers {"Accept" "application/transit+json"}
     :handler #(reset! messages (vec %))}))

(defn- send-message! [fields errors messages]
  (POST
    "/add-message"
    {:format :json
     :headers
     {"Accept" "application/transit+json"
      "x-csrf-token" (.-value (.getElementById js/document "token"))}
     :params @fields
     :handler #(do
                 (reset! errors nil)
                 (swap! messages conj (assoc @fields :timestamp (js/Date.))))
     :error-handler #(do
                       (.error js/console (str "error:" %))
                       (reset! errors (get-in % [:response :errors])))}))

(defn- errors-component [errors id]
  (when-let [error (id @errors)]
    [:div.alert.alert-danger (clojure.string/join error)]))

(defn- message-list [messages]
  [:ul.content
   (for [{:keys [timestamp message name]} @messages]
     ;; following annotation allows Reagent to efficiently check whether a particular element
     ;; needs to be re-rendered - if ommited, it could result in performance degradation for large lists
     ^{:key timestamp}
     [:li
      [:time (.toLocaleString timestamp)]
      [:p message]
      [:p " - " name]])])

(defn- message-form [messages]
  (let [fields (atom {})
        errors (atom nil)]
    (fn []
      [:div.content
       [:div.form-group
        [errors-component errors :name]
        [:p "Name:"
         [:input.form-control
          {:type :text
           :name :name
           :on-change #(swap! fields assoc :name (-> % .-target .-value))
           :value (:name @fields)}]]]
       [errors-component errors :message]
       [:p "Message:"
        [:textarea.form-control
         {:rows 4
          :cols 4
          :name :message
          :on-change #(swap! fields assoc :message (-> % .-target .-value))}
         (:message @fields)]]
       [:input.btn.btn-primary {:type :submit
                                :on-click #(send-message! fields errors messages)
                                :value "comment"}]])))

(defn home []
  (let [messages (atom nil)]
    (get-messages messages)
    (fn []
      [:div
       [:div.row
        [:div.span12
         [message-list messages]]]
       [:div row
        [:div.span12
         [message-form messages]]]])))

(r/render
 [home]
 (.getElementById js/document "content"))
