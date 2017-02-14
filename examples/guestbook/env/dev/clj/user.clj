(ns user
  (:require [mount.core :as mount]
            guestbook.core))

(defn start []
  (mount/start-without #'guestbook.core/http-server
                       #'guestbook.core/repl-server))

(defn stop []
  (mount/stop-except #'guestbook.core/http-server
                     #'guestbook.core/repl-server))

(defn restart []
  (stop)
  (start))


