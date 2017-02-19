(ns ring-app.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.http-response :as r]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn handler [request-map]
  (r/ok
   (str "<html><body>your IP is:" (:remote-addr request-map) "</body></html>")))

(defn wrap-nocache
  "middleware which wraps response with 'Pragma: no-cache' header"
  [handler]
  (fn [request]
    (-> request
        handler
        (assoc-in [:headers "Pragma"] "no-cache"))))

(defn -main []
  (jetty/run-jetty
   (-> handler
       var ;; notice that we need to create var from handler for wrap-reload to work
       wrap-nocache
       wrap-reload)
   {:port 3000
    :join? false}))
