(ns guestbook.routes.home
  (:require [bouncer
             [core :as b]
             [validators :as v]]
            [compojure.core :refer [defroutes GET POST]]
            [guestbook.db.core :as db]
            [guestbook.layout :as layout]
            [ring.util.response :refer [response status]]))

(defn home-page []
  (layout/render "home.html"))

(defn validate-message
  "Check if incoming message is valid to be stored in DB."
  [params]
  (first (b/validate
          params
          :name v/required
          :message [v/required [v/min-count 10]])))

(defn save-message! [{:keys [params]}]
  (if-let [errors (validate-message params)]
    (-> {:errors errors}
        response
        (status 400))
    (do
      (db/save-message!
       (assoc params :timestamp (java.util.Date.)))
      (response {:status :ok}))))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" request (home-page))
  (GET "/messages" [] (response (db/get-messages)))
  (POST "/add-message" request (save-message! request))
  (GET "/about" [] (about-page)))

