(ns db-examples.core
  (:require [clojure.java.jdbc :as sql])
  (:import org.postgresql.ds.PGPoolingDataSource))

(def db {:subprotocol "postgresql"
         :subname "//localhost/reporting"
         :user "admin"
         :password "admin"})

;; another option is to provide a JDBC data source and configure it manually
;; -> useful for any driver-specific parameters not accessible through the idiomatic
;;    parameter map
#_(def db
    {:datasource
     (doto (PGPoolingDataSource.)
       (.setServerName "localhost")
       (.setDatabaseName "my_website")
       (.setUser "admin")
       (.setPassword "admin")
       (.setMaxConnections 10))})

;; finally, we can define a JNDI string for a connection managed by application server
#_(def db {:name "jdbc/myDatasource"})

;;; Creating tables

(defn create-users-table! []
  (sql/db-do-commands
   db
   (sql/create-table-ddl
    :users
    [[:id "varchar(32) PRIMARY KEY"]
     [:pass "varchar(100)"]])))

(try (create-users-table!)
     (catch Exception e (.getNextException e)))

;;; Selecting Records
;;; query function allows us to work with returned data without having to load the entire result into memory
;;; (lazy sequence)

(defn get-user [id]
  (first (sql/query db ["select * from users where id = ?" id])))
(get-user "foo")

;;; Inserting Records

;; insert!
(defn add-user! [user]
  (sql/insert! db :users user))
(add-user! {:id "foo" :pass "bar"})

;; insert multiple records - insert-multi!
(defn add-users! [& users]
  (sql/insert-multi! db :users users))
(add-users!
 {:id "foo1" :pass "bar"}
 {:id "foo2" :pass "bar"}
 {:id "foo3" :pass "bar"})
