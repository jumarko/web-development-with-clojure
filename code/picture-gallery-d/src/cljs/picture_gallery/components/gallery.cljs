;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns picture-gallery.components.gallery
  (:require [reagent.core :refer [atom]]
            [reagent.session :as session]
            [ajax.core :as ajax]
            [clojure.string :as s]
            [picture-gallery.components.common :as c]))

(defn image-modal [link]
  (fn []
    [:div
     [:img.image.panel.panel-default
      {:on-click #(session/remove! :modal)
       :src link}]
     [:div.modal-backdrop.fade.in]]))

(defn thumb-link [{:keys [owner name]}]
  [:div.col-sm-4>img
   {:src      (str js/context "/gallery/" owner "/" name)
    :on-click #(session/put!
                 :modal
                 (image-modal
                   (str js/context "/gallery/" owner "/"
                        (s/replace name #"thumb_" ""))))}])

(defn gallery [links]
  [:div.text-xs-center
   (for [row (partition-all 3 links)]
     ^{:key row}
     [:div.row
      (for [link row]
        ^{:key link}
        [thumb-link link])])])

(defn forward [i pages]
  (if (< i (dec pages)) (inc i) i))

(defn back [i]
  (if (pos? i) (dec i) i))

(defn nav-link [page i]
  [:li.page-item>a.page-link.btn.btn-primary
   {:on-click #(reset! page i)
    :class    (when (= i @page) "active")}
   [:span i]])

(defn pager [pages page]
  (when (> pages 1)
    (into
      [:div.text-xs-center>ul.pagination.pagination-lg]
      (concat
        [[:li.page-item>a.page-link.btn.btn-primary
          {:on-click #(swap! page back pages)
           :class    (when (= @page 0) "disabled")}
          [:span "«"]]]
        (map (partial nav-link page) (range pages))
        [[:li.page-item>a.page-link.btn.btn-primary
          {:on-click #(swap! page forward pages)
           :class    (when (= @page (dec pages)) "disabled")}
          [:span "»"]]]))))

(defn fetch-gallery-thumbs! [owner]
  (ajax/GET (str "/list-thumbnails/" owner)
            {:handler #(session/put! :thumbnail-links %)}))

(defn partition-links [links]
  (when (not-empty links)
    (vec (partition-all 6 links))))

(defn gallery-page []
  (let [page (atom 0)]
    (fn []
      [:div.container
       (when-let [thumbnail-links (partition-links (session/get :thumbnail-links))]
         [:div.row>div.col-md-12
          [pager (count thumbnail-links) page]
          [gallery (thumbnail-links @page)]])])))
