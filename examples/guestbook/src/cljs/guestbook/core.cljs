(ns guestbook.core
  (:require [ajax.core :refer [GET]]
            [guestbook.ws :as ws]
            [reagent.core :as r :refer [atom]]))

;; Let's reimplement the form in Reagent

(defn- get-messages [messages]
  (GET "/messages"
    {:headers {"Accept" "application/transit+json"}
     :handler #(reset! messages (vec %))}))

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

(defn- message-form [fields errors]
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
                            :on-click #(ws/send-message! [:guestbook/add-message @fields] 8000)
                            :value "comment"}]])

(defn response-handler [messages fields errors]
  ;; data contains message id and payload
  (fn [{[_ message] :?data}]
    (if-let [reponse-errors (:errors messages)]
      (reset! errors response-errors)
      (do
        (reset! errors nil)
        (reset! fields nil)
        (swap! messages conj message)))))

(defn home []
  (let [messages (atom nil)
        errors (atom nil)
        fields (atom nil)]
    (ws/start-router! (response-handler messages fields errors))
    (get-messages messages)
    (fn []
      [:div
       [:div.row
        [:div.span12
         [message-list messages]]]
       [:div row
        [:div.span12
         [message-form fields errors]]]])))

(r/render
 [home]
 (.getElementById js/document "content"))
