(ns reporting-examples.routes.home
  (:require [reporting-examples.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.response :as resp]
            [clojure.java.io :as io]
            [reporting-examples.reports :as reports]
            [ring.util.http-response :as response]))

(defn- write-response [report-bytes]
  (with-open [in (java.io.ByteArrayInputStream. report-bytes)]
    (-> (resp/response in)
        (resp/header "Content-Disposition" "filename=document.pdf")
        (resp/header "Content-Length" (count report-bytes))
        (resp/content-type "application/pdf"))))

(defn- generate-report [report-type]
  (try
    (let [out (java.io.ByteArrayOutputStream.)]
      (condp = (keyword report-type)
        :table (reports/table-report out)
        :list (reports/list-report out))
      (write-response (.toByteArray out)))
    (catch Exception ex
      (layout/render "home.html" {:error (.getMessage ex)}))))

(defn home-page []
  (layout/render "home.html"))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))
  (GET "/:report-type" [report-type] (generate-report report-type)))
