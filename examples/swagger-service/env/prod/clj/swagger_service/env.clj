(ns swagger-service.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[swagger-service started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[swagger-service has shut down successfully]=-"))
   :middleware identity})
