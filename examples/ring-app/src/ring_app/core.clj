(ns ring-app.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.http-response :as r]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [compojure.core :as c]))

(defn- response-handler [request]
  (r/ok
   (str "<html><body> your IP is: " (:remote-addr request) " </body></html>")))

(c/defroutes handler
  (c/GET "/" request response-handler)
  ;; example from https://github.com/weavejester/compojure/wiki/Destructuring-Syntax
  (c/GET "/foo/foo3/:id" [id greeting] (str "<h1>" greeting " user " id " </h1>"))
  (c/GET "/:id" [id] (str "<p>the id is: " id " </p>"))
  ;; notice that output of following is a little bit strange with additional commas in quotes between elements
  (c/GET "/foo/foo" request (interpose ", " (keys request)))
  (c/POST "/foo/foo2" [x y :as request] (str x y request))
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
       (wrap-defaults site-defaults)
       wrap-nocache
       wrap-reload
       wrap-formats)
   {:port 3000
    :join? false}))
