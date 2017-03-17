(ns swagger-service.app
  (:require [swagger-service.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
