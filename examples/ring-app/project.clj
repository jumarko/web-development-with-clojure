(defproject ring-app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring "1.5.1"]
                 [metosin/ring-http-response "0.8.1"]
                 [ring-middleware-format "0.7.2"]
                 [compojure "1.5.2"]
                 [ring/ring-defaults "0.2.2"]]
  :main ring-app.core)
