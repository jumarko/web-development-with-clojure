(ns reporting-examples.reports
  (:require [reporting-examples.db.core :as db]
            [clj-pdf.core :refer [pdf template]]))

;;; testing
#_(pdf
   [{:header "Wow, that was sexy!"}
    [:list
     [:chunk {:style :bold} "a bold item"]
     "another item"
     "yet another item"]
    [:paragraph "I'm a paragraph"]]
   "doc.pdf")

;;; generate PDF for all employess

(def employee-template (template [$name $occupation $place $country]))
;; test the template
#_(take 2 (employee-template (take 2 (db/read-employees))))
;; generate pdf
#_(pdf
 [{:header "Employee List"}
  (into [:table
         {:border false
          :cell-border false
          :header [{:backdrop-color [0 150 150]} "Name" "Occupation" "Place" "Country"]}]
        (employee-template (db/read-employees)))]
 "report.pdf")

(defn table-report [out]
  (pdf
   [{:header "Employee List"}
    (into [:table
           {:border false
            :cell-border false
            :header [{:backdrop-color [0 150 150]} "Name" "Occupation" "Place" "Country"]}]
          (employee-template (db/read-employees)))]
   out))

;; previous PDF is great but template is boring => let's create a more interesting one
(def employee-template-paragraph
  (template
   [:paragraph
    [:heading {:style {:size 15}} $name]
    [:chunk {:style :bold} "occupation: "] $occupation "\n"
    [:chunk {:style :bold} "place: "] $place "\n"
    [:chunk {:style :bold} "country: "] $country
    [:spacer]]))
#_(pdf
 [{}
  [:heading {:size 10} "Employees"]
  [:line]
  [:spacer]
  (employee-template-paragraph (db/read-employees))]
 "report.pdf")
(defn list-report [out]
  (pdf
   [{}
    [:heading {:size 10} "Employees"]
    [:line]
    [:spacer]
    (employee-template-paragraph (db/read-employees))]
   out))
