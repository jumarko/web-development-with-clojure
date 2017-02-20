(ns ring-app.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.http-response :as r]
            [ring.middleware.format :refer [wrap-restful-format]]
            [compojure.core :as c]))

(defn- response-handler [request]
  (r/ok
   (str "<html><body> your IP is: " (:remote-addr request) " </body></html>")))

(c/defroutes handler
  (c/GET "/" request response-handler)
  (c/GET "/:id" [id] (str "<p>the id is: " id " </p>"))
  (c/POST "/json" [id] (r/ok {:result id})))

;; compojure also allows us to avoid repetition using context macro
(defn display-profile [id] )
(defn display-settings [id] )
(defn change-password-page [id] )

(c/defroutes user-routes
  (c/context "/user/:id" [id]
    (c/GET "/profile" [] (display-profile id))
    (c/GET "/settings" [] (display-settings id))
    (c/GET "/change-password" [] (change-password-page id))))

(defn wrap-nocache
  "middleware which wraps response with 'Pragma: no-cache' header"
  [handler]
  (fn [request]
    (-> request
        handler
        (assoc-in [:headers "Pragma"] "no-cache"))))

(defn wrap-formats
  "Add JSON format"
  [handler]
  (wrap-restful-format
   handler
   {:formats [:json-kw :transit-json :transit-msgpack]}))

(defn -main []
  (jetty/run-jetty
   (-> handler
       var ;; notice that we need to create var from handler for wrap-reload to work
       wrap-nocache
       wrap-reload
       wrap-formats)
   {:port 3000
    :join? false}))
