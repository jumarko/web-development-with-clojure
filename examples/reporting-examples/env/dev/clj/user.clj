(ns user
  (:require [mount.core :as mount]
            reporting-examples.core))

(defn start []
  (mount/start-without #'reporting-examples.core/http-server
                       #'reporting-examples.core/repl-server))

(defn stop []
  (mount/stop-except #'reporting-examples.core/http-server
                     #'reporting-examples.core/repl-server))

(defn restart []
  (stop)
  (start))


