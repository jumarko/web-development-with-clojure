(ns picture-gallery.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [picture-gallery.layout :refer [error-page]]
            [picture-gallery.routes.home :refer [home-routes]]
            [picture-gallery.routes.services :refer [service-routes]]
            [compojure.route :as route]
            [picture-gallery.env :refer [defaults]]
            [mount.core :as mount]
            [picture-gallery.middleware :as middleware]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    (-> #'home-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    #'service-routes
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
