(ns picture-gallery.app
  (:require [picture-gallery.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
