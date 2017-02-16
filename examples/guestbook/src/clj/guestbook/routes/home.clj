(ns guestbook.routes.home
  (:require [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET POST]]
            [guestbook.db.core :as db]
            [guestbook.layout :as layout]))

(defn home-page []
  (layout/render
   "home.html"
   {:messages (db/get-messages)}))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (POST "/message" request (db/save-message! request))
  (GET "/about" [] (about-page)))

