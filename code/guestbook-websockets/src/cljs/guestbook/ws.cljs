;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns guestbook.ws
  (:require [cognitect.transit :as t]))

(defonce ws-chan (atom nil))
(def json-reader (t/reader :json))
(def json-writer (t/writer :json))

(defn receive-message! [handler]
  (fn [msg]
    (->> msg .-data (t/read json-reader) handler)))

(defn send-message! [msg]
  (if @ws-chan
    (->> msg (t/write json-writer) (.send @ws-chan))
    (throw (js/Error. "WebSocket is not available!"))))

(defn connect! [url receive-handler]
  (if-let [chan (js/WebSocket. url)]
    (do
      (set! (.-onmessage chan) (receive-message! receive-handler))
      (reset! ws-chan chan))
    (throw (js/Error. "WebSocket connection failed!"))))
