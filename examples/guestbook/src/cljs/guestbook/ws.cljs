(ns guestbook.ws
  (:require [cognitect.transit :as t]))

;; atom that holds the websocket channel
(defonce ws-channel (atom nil))

;;; serialization helpers
(def json-reader (t/reader :json))
(def json-writer (t/writer :json))

;; deserialize message before passing it to the handler
(defn receive-message! [handler]
  (fn [message]
    (->> message
         .-data
         (t/read json-reader)
         handler)))

;; send transit-encoded message through channel
(defn send-message! [message]
  (if @ws-channel
    (->> message
         (t/write json-writer)
         (.send @ws-channel))
    (throw (js/Error. "WebSocket is not available!"))))

(defn connect! [url handler]
  (if-let [channel (js/WebSocket. url)]
    (do
      (set! (.-onmessage channel) (receive-message! handler))
      (reset! ws-channel channel))
    (throw (js/Error. "WebSocket connection failed!"))))
