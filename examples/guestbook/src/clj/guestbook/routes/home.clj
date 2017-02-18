(ns guestbook.routes.home
  (:require [bouncer
             [core :as b]
             [validators :as v]]
            [compojure.core :refer [defroutes GET POST]]
            [guestbook.db.core :as db]
            [guestbook.layout :as layout]
            [ring.util.http-response :as response]))

(defn home-page [{:keys [flash]}]
  (layout/render
   "home.html"
   (merge {:messages (db/get-messages)}
          (select-keys flash [:name :message :errors]))))

(defn validate-message
  "Check if incoming message is valid to be stored in DB."
  [params]
  (first (b/validate
   params
   :name v/required
   :message [v/required [v/min-count 10]])))

(defn save-message! [{:keys [params]}]
  (if-let [errors (validate-message params)]
    (do
    (-> (response/found "/")
        (assoc :flash (assoc params :errors errors)))
    )
    (do
      (db/save-message!
       (assoc params :timestamp (java.util.Date.)))
      (response/found "/"))))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" request (home-page request))
  (POST "/message" request (save-message! request))
  (GET "/about" [] (about-page)))

