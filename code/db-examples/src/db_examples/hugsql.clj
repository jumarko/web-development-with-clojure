;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns db-examples.hugsql
  (:require [db-examples.core :refer [db]]
            [clojure.java.jdbc :as sql]
            [hugsql.core :as hugsql]))

(hugsql/def-db-fns "users.sql")

(defn add-user-transaction [user]
  (sql/with-db-transaction [t-conn db]
    (if-not (find-user t-conn {:id (:id user)})
            (add-user! t-conn user))))
