(ns reporting-examples.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[reporting-examples started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[reporting-examples has shut down successfully]=-"))
   :middleware identity})
