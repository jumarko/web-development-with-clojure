;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns picture-gallery.core
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [markdown.core :refer [md->html]]
            [picture-gallery.ajax :refer [load-interceptors!]]
            [ajax.core :refer [GET POST]]
            [picture-gallery.components.registration :as reg])
  (:import goog.History))

(defn nav-link [uri title page collapsed?]
  [:li.nav-item
   {:class (when (= page (session/get :page)) "active")}
   [:a.nav-link
    {:href uri
     :on-click #(reset! collapsed? true)} title]])

(defn user-menu []
  (if-let [id (session/get :identity)]
    [:ul.nav.navbar-nav.pull-xs-right
     [:li.nav-item
      [:a.dropdown-item.btn
       {:on-click #(session/remove! :identity)}
       [:i.fa.fa-user] " " id " | sign out"]]]
    [:ul.nav.navbar-nav.pull-xs-right
     [:li.nav-item [reg/registration-button]]]))

(defn navbar []
  (let [collapsed? (r/atom true)]
    (fn []
      [:nav.navbar.navbar-light.bg-faded
       [:button.navbar-toggler.hidden-sm-up
        {:on-click #(swap! collapsed? not)} "☰"]
       [:div.collapse.navbar-toggleable-xs
        (when-not @collapsed? {:class "in"})
        [:a.navbar-brand {:href "#/"} "picture-gallery"]
        [:ul.nav.navbar-nav
         [nav-link "#/" "Home" :home collapsed?]
         [nav-link "#/about" "About" :about collapsed?]]]
       [user-menu]])))

(defn about-page []
  [:div "this is the story of picture-gallery... work in progress"])

(defn home-page []
  [:div.container
   [:div.jumbotron
    [:h1 "Welcome to picture-gallery"]]
   [:div.row
    [:div.col-md-12
     [:h2 "TODO: display pictures"]]]])

(def pages
  {:home  #'home-page
   :about #'about-page})

(defn modal []
  (when-let [session-modal (session/get :modal)]
    [session-modal]))

(defn page []
  [:div
   [modal]
   [(pages (session/get :page))]])
;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
                    (session/put! :page :home))

(secretary/defroute "/about" []
                    (session/put! :page :about))

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
(defn mount-components []
  (r/render [#'navbar] (.getElementById js/document "navbar"))
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (load-interceptors!)
  (hook-browser-navigation!)
  (mount-components))

