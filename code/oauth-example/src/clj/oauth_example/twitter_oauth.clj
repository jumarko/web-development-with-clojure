;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns oauth-example.twitter-oauth
  (:require [oauth.client :as oauth]
            [oauth-example.config :refer [env]]))

;;have to set Callback URL in Twitter app settings
(def request-token-uri
  "https://api.twitter.com/oauth/request_token")

(def access-token-uri
  "https://api.twitter.com/oauth/access_token")

(def authorize-uri
  "https://api.twitter.com/oauth/authenticate")

(def consumer
  (oauth/make-consumer (env :twitter-consumer-key)
                       (env :twitter-consumer-secret)
                       request-token-uri
                       access-token-uri
                       authorize-uri
                       :hmac-sha1))

(defn oauth-callback-uri
  "Generates the Twitter oauth request callback URI"
  [{:keys [headers]}]
  (str (headers "x-forwarded-proto")
       "://" (headers "host")
       "/oauth/twitter-callback"))

(defn fetch-request-token
  "Fetches a request token."
  [request]
  (->> request
       oauth-callback-uri
       (oauth/request-token consumer)
       :oauth_token))

(defn fetch-access-token
  [request_token]
  (oauth/access-token consumer request_token (:oauth_verifier request_token)))

(defn auth-redirect-uri
  "Gets the URI the user should be redirected to when authenticating with Twitter."
  [request-token]
  (str (oauth/user-approval-uri consumer request-token)))
