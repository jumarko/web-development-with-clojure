(ns ring-app.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]))

(defn handler [request-map]
  (prn request-map)
  (response/response
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
   (wrap-nocache handler)
   {:port 3000
    :join? false}))
