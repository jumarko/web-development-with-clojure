;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns picture-gallery.components.common
  (:require [reagent.core :refer [atom]]
            [reagent.session :as session]))

(defn modal [header body footer]
  [:div
   [:div.modal-dialog
    [:div.modal-content
     [:div.modal-header [:h3 header]]
     [:div.modal-body body]
     [:div.modal-footer
      [:div.bootstrap-dialog-footer
       footer]]]]
   [:div.modal-backdrop.fade.in]])

(defn error-modal []
  (when-let [error (session/get :error)]
    [modal
     (:message error)
     [:div.container-fluid
      [:div.form-group
       [:div.alert.alert-danger (:cause error)]]]
     [:div.form-group
      [:button.btn.btn-danger.btn-lg.btn-block
       {:type     "submit"
        :on-click #(session/remove! :error)}
       "OK"]]]))

(defn input [type id placeholder fields]
  [:input.form-control.input-lg
   {:type        type
    :placeholder placeholder
    :value       (id @fields)
    :on-change   #(swap! fields assoc id (-> % .-target .-value))}])

(defn form-input [type label id placeholder fields optional?]
  [:div.form-group
   [:label label]
   (if optional?
     [input type id placeholder fields]
     [:div.input-group
      [input type id placeholder fields]
      [:span.input-group-addon
       "✱"]])])

(defn text-input [label id placeholder fields & [optional?]]
  (form-input :text label id placeholder fields optional?))

(defn email-input [label id placeholder fields & [optional?]]
  (form-input :email label id placeholder fields optional?))

(defn password-input [label id placeholder fields & [optional?]]
  (form-input :password label id placeholder fields optional?))
