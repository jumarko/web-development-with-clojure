;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns db-examples.core
  (:require [clojure.java.jdbc :as sql]))

(def db {:subprotocol "postgresql"
         :subname "//localhost/reporting"
         :user "admin"
         :password "admin"})

(defn create-users-table! []
  (sql/db-do-commands db
    (sql/create-table-ddl
      :users
      [[:id "varchar(32) PRIMARY KEY"]
       [:pass "varchar(100)"]])))

(defn get-user [id]
  (first (sql/query db ["select * from users where id = ?" id])))

(defn add-user! [user]
  (sql/insert! db :users user))

(defn add-users! [& users]
  (sql/insert-multi! db :users users))

(defn set-pass! [id pass]
  (sql/update!
    db
    :users
    {:pass pass}
    ["id=?" id]))

(defn remove-user! [id]
  (sql/delete! db :users ["id=?" id]))
