(ns db-examples.hugsql
  (:require [clojure.java.jdbc :as sql]
            [db-examples.core :refer [db]]
            [hugsql.core :as hugsql]))

(hugsql/def-db-fns "users.sql")

(add-user! db {:id "foogen" :pass "bar"})
(add-user-returning! db {:id "foogen-returning" :pass "bar"})
(add-users! db {:users [["bob" "Bob"]
                        ["alice" "Alice"]]})
(find-user db {:id "bob"})
;; in-list query
(find-users db {:ids ["foo" "bob" "alice"]})

;; easy mix with clojure.java.jdbc - e.g. run withing transaction
(defn add-user-transaction [user]
  (sql/with-db-transaction [t-conn db]
    (if-not (find-user t-conn {:id (:id user)})
      (add-user! t-conn user))))
(add-user-transaction {:id "malice" :pass "MaliciousAlice"})
