(ns guestbook.routes.home
  (:require [compojure.core :refer [defroutes GET POST]]
            [guestbook.db.core :as db]
            [guestbook.layout :as layout]
            [ring.util.http-response :as response]))

(defn home-page []
  (layout/render
   "home.html"
   {:messages (db/get-messages)}))

(defn save-message! [{:keys [params]}]
  (db/save-message!
   (assoc params :timestamp (java.util.Date.)))
   (response/found "/"))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (POST "/message" request (save-message! request))
  (GET "/about" [] (about-page)))

