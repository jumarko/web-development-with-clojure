(ns guestbook.routes.ws
  (:require [compojure.core :refer [GET defroutes]]
            [clojure.tools.logging :as log]
            [immutant.web.async :as async]
            [cognitect.transit :as transit]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [guestbook.db.core :as db]))

(defonce channels (atom #{}))

(defn- connect! [channel]
  (log/info "Channel open")
  (swap! channels conj channel))

(defn- disconnect! [channel {:keys [code reason]}]
  (log/info "close code:" code "reason: " reason)
  ;; why not use `disj` function?
  ;; (swap! channels disj channel)
  (swap! channels clojure.set/difference #{channel}))


;;; let's create helper functions for encoding / decoding messages sent via WebSockets

(defn encode-transit [message]
  (let [out (java.io.ByteArrayOutputStream. 4096)
        writer (transit/writer out :json)]
    (transit/write writer message)
    (.toString out)))

(defn decode-transit [message]
  (let [in (java.io.ByteArrayInputStream. (.getBytes message))
        reader (transit/reader in :json)]
    (transit/read reader)))


