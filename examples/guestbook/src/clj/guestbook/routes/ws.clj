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

(defn validate-message
  "Check if incoming message is valid to be stored in DB."
  [message]
  (first (b/validate
          message
          :name v/required
          :message [v/required [v/min-count 10]])))

(defn save-message! [message]
  (if-let [errors (validate-message message)]
    {:errors errors}
    (do
      (db/save-message! message)
      ;; save-message! no longer needs to generate Ring response, so just return plain message
      message)))

(defn handle-message! [channel message]
  (let [response (-> message
                     decode-transit
                     (assoc :timestamp (java.util.Date.))
                     save-message!)]
    (if (:errors response)
      (async/send! channel (encode-transit response))
      (doseq [channel @channels]
        (async/send! channel (encode-transit response))))))

;; as-channel will create the actual WebSocket
(defn ws-handler [request]
  (async/as-channel
   request
   {:on-open connect!
    :on-close disconnect!
    :on-message handle-message!}))

(defroutes websocket-routes
  (GET "/ws" [] ws-handler))
