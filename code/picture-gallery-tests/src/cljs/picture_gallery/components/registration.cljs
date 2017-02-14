;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns picture-gallery.components.registration
  (:require [reagent.core :refer [atom]]
            [reagent.session :as session]
            [ajax.core :as ajax]
            [picture-gallery.components.common :as c]
            [picture-gallery.validation :refer [registration-errors]]))

(defn register! [fields errors]
  (reset! errors (registration-errors @fields))
  (when-not @errors
    (ajax/POST "/register"
               {:params @fields
                :handler
                        #(do
                          (session/put! :identity (:id @fields))
                          (reset! fields {})
                          (session/remove! :modal))
                :error-handler
                        #(reset! errors {:server-error (get-in % [:response :message])})})))

(defn delete-account! []
  (ajax/DELETE "/account"
             {:handler #(do
                          (session/remove! :identity)
                          (session/put! :page :home))}))

(defn delete-account-modal []
  (fn []
    [c/modal
     [:h2.alert.alert-danger "Delete Account!"]
     [:p "Are you sure you wish to delete the account and associated gallery?"]
     [:div
      [:button.btn.btn-primary
       {:on-click (fn []
                    (delete-account!)
                    (session/remove! :modal))}
       "Delete"]
      [:button.btn.btn-danger
       {:on-click (fn [] (session/remove! :modal))}
       "Cancel"]]]))

(defn registration-form []
  (let [fields (atom {})
        error  (atom nil)]
    (fn []
      [c/modal
       [:div "Picture Gallery Registration"]
       [:div
        [:div.well.well-sm
         [:strong "✱ required field"]]
        [c/text-input "name" :id "enter a user name" fields]
        (when-let [error (first (:id @error))]
          [:div.alert.alert-danger error])
        [c/password-input "password" :pass "enter a password" fields]
        (when-let [error (first (:pass @error))]
          [:div.alert.alert-danger error])
        [c/password-input "password" :pass-confirm "re-enter the password" fields]
        (when-let [error (:server-error @error)]
          [:div.alert.alert-danger error])]
       [:div
        [:button.btn.btn-primary
         {:on-click #(register! fields error)}
         "Register"]
        [:button.btn.btn-danger
         {:on-click #(session/remove! :modal)}
         "Cancel"]]])))

(defn registration-button []
  [:a.btn
   {:on-click #(session/put! :modal registration-form)}
   "register"])
