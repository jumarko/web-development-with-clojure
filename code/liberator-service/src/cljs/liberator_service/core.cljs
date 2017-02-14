;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns liberator-service.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [ajax.core :refer [GET POST]]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType])
    (:import goog.History))

;; -------------------------
;; Views

(defn error-component []
  (when-let [error (session/get :error)]
    [:p error]))

(defn item-list [items]
  (when (not-empty items)
    [:ul
     (for [item items]
       ^{:key item}
       [:li item])]))

(defn parse-items [items]
  (->> items
       clojure.string/split-lines
       (remove empty?)
       vec))

(defn get-items []
  (GET "/items"
       {:error-handler
        #(session/put! :error (:response %))
        :handler
        #(session/put! :items (parse-items %))}))

(defn add-item! [item]
  (session/remove! :error)
  (POST "/add-item"
        {:headers {"x-csrf-token"
                   (.-value (.getElementById js/document "__anti-forgery-token"))}
         :format :raw
         :params {:item (str @item)}
         :error-handler #(session/put! :error (:response %))
         :handler #(do
                     (println "updating")
                     (session/update-in! [:items] conj @item)
                     (reset! item nil))}))

(defn item-input-component []
  (let [item (atom nil)]
    (fn []
      [:div
       [:input
        {:type :text
         :value @item
         :on-change #(reset! item (-> % .-target .-value))
         :placeholder "To-Do item"}]
       [:button
        {:on-click #(add-item! item)}
        "Add To-Do"]])))

(defn home-page []
  [:div
   [:h2 "To-Do Items"]
   [error-component]
   [item-list (session/get :items)]
   [item-input-component]])

(defn about-page []
  [:div [:h2 "About liberator-service"]
   [:div [:a {:href "#/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (get-items)
  (mount-root))
