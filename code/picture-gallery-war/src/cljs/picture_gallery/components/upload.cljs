;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns picture-gallery.components.upload
  (:require [goog.events :as gev]
            [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [picture-gallery.components.common :as c])
  (:import goog.net.IframeIo
           goog.net.EventType
           [goog.events EventType]))

(defn upload-file! [upload-form-id status]
  (reset! status nil)
  (let [io (IframeIo.)]
    (gev/listen
      io goog.net.EventType.SUCCESS
      #(reset! status [:div.alert.alert-success "file uploaded successfully"]))
    (gev/listen
      io goog.net.EventType.ERROR
      #(reset! status [:div.alert.alert-danger "failed to upload the file"]))
    (.setErrorChecker io #(= "error" (.getResponseText io)))
    (.sendFromForm
      io
      (.getElementById js/document upload-form-id)
      "/upload")))

(defn upload-form []
  (let [status (atom nil)
        form-id "upload-form"]
    (fn []
      [c/modal
       [:div "Upload File"]
       [:div
        (when @status @status)
        [:form {:id       form-id
                :enc-type "multipart/form-data"
                :method   "POST"}
         [:fieldset.form-group
          [:label {:for "file"} "select an image for upload"]
          [:input.form-control {:id "file" :name "file" :type "file"}]]]
        [:button.btn.btn-primary
         {:on-click #(upload-file! form-id status)}
         "Upload"]
        [:button.btn.btn-danger.pull-right
         {:on-click #(session/remove! :modal)}
         "Cancel"]]])))

(defn upload-button []
  [:a.btn
   {:on-click #(session/put! :modal upload-form)}
   "upload image"])
