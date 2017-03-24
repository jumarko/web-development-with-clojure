(ns reporting-examples.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [reporting-examples.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[reporting-examples started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[reporting-examples has shut down successfully]=-"))
   :middleware wrap-dev})
