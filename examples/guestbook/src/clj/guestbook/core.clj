(ns guestbook.core
  (:gen-class)
  (:require :as
            [cider.nrepl :refer [cider-middleware]]
            [clojure.tools
             [cli :refer [parse-opts]]
             [logging :as log]]
            [clojure.tools.nrepl.server :as nrepl]
            [guestbook
             [config :refer [env]]
             [handler :as handler]]
            [luminus
             [http-server :as http]
             [repl-server :as repl]]
            [luminus-migrations.core :as migrations]
            [mount.core :as mount]
            refactor-nrepl.middleware
            [clojure.string :refer [join]]))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :parse-fn #(Integer/parseInt %)]])

(mount/defstate ^{:on-reload :noop}
  http-server
  :start
  (http/start
   (-> env
       (assoc :handler (handler/app))
       (update :port #(or (-> env :options :port) %))))
  :stop
  (http/stop http-server))

(defn cider&cljr-nrepl-handler []
  (apply nrepl/default-handler (cons #'refactor-nrepl.middleware/wrap-refactor
                                     (map resolve cider-middleware))))

(mount/defstate ^{:on-reload :noop}
  repl-server
  :start
  (when-let [nrepl-port (env :nrepl-port)]
    (repl/start {:port nrepl-port
                 :handler (cider&cljr-nrepl-handler)}))
  :stop
  (when repl-server
    (repl/stop repl-server)))

(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents))

(defn start-app [args]
  (doseq [component (-> args
                        (parse-opts cli-options)
                        mount/start-with-args
                        :started)]
    (log/info component "started"))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& args]
  (cond
    (some #{"migrate" "rollback"} args)
    (do
      (mount/start #'guestbook.config/env)
      (migrations/migrate args (select-keys env [:database-url]))
      (System/exit 0))
    :else
    (start-app args)))

