(ns guestbook.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [guestbook.layout :refer [error-page]]
            [guestbook.routes.home :refer [home-routes]]
            [guestbook.routes.ws :refer [websocket-routes]]
            [compojure.route :as route]
            [guestbook.env :refer [defaults]]
            [mount.core :as mount]
            [guestbook.middleware :as middleware]))

(mount/defstate init-app
  :start ((or (:init defaults) identity))
  :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
   #'websocket-routes
   (-> #'home-routes
       (wrap-routes middleware/wrap-csrf)
       (wrap-routes middleware/wrap-formats))
   (wrap-routes #'home-routes middleware/wrap-csrf)
   (route/not-found
    (:body
     (error-page {:status 404
                  :title "page not found"})))))

(defn app [] (middleware/wrap-base #'app-routes))
