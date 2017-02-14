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
            [ajax.core :as ajax]
            [picture-gallery.components.registration :as reg]
            [picture-gallery.components.login :as l]
            [picture-gallery.components.upload :as u])
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
     [:li.nav-item [u/upload-button]]
     [:li.nav-item
      [:a.dropdown-item.btn
       {:on-click #(ajax/POST
                     "/logout"
                     {:handler (fn [] (session/remove! :identity))})}
       [:i.fa.fa-user] " " id " | sign out"]]]
    [:ul.nav.navbar-nav.pull-xs-right
     [:li.nav-item [l/login-button]]
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

(defn galleries [gallery-links]
  [:div.text-xs-center
   (for [row (partition-all 3 gallery-links)]
     ^{:key row}
     [:div.row
      (for [{:keys [owner name]} row]
        ^{:key (str owner name)}
        [:div.col-sm-4
         [:a {:href (str "#/gallery/" owner)}
          [:img {:src (str js/context "/gallery/" owner "/" name)}]]])])])

(defn list-galleries! []
  (ajax/GET "/list-galleries"
            {:handler #(session/put! :gallery-links %)}))

(defn home-page []
  (list-galleries!)
  (fn []
    [:div.container
     [:div.row
      [:div.col-md-12>h2 "Available Galleries"]]
     (when-let [gallery-links (session/get :gallery-links)]
       [:div.row>div.col-md-12
        [galleries gallery-links]])]))

(def pages
  {:home    #'home-page
   :about   #'about-page})

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
  (session/put! :identity js/identity)
  (mount-components))
