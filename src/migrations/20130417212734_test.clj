(ns migrations.20130417212734-test
  (:use [korma db core])
  (:require [learn-smthng.models.schema :as schema]
            [clojure.java.jdbc :as sql]))

(defdb db (postgres schema/db-spec))
(defentity users-table)

(defn create-tables
  "Create users table"
  []
  (sql/create-table :users
                  [:id :serial "PRIMARY KEY"]
                  [:email "varchar(50)" "NOT NULL"]
                  [:password "varchar(30)" "NOT NULL"]))

(defn drop-tables
  "Drop users table"
  []
  (sql/drop-table :users))

(defn invoke-with-connection [f]
  (sql/with-connection
     (get-connection db)
     (sql/transaction
       (f))))

(defn up
  "Migrates the database up to version 20130417212734."
  []
  (println "migrations.20130417212734-test up...")
  (invoke-with-connection create-tables))

(defn down
  "Migrates the database down from version 20130417212734."
  []
  (println "migrations.20130417212734-test down...")
  (invoke-with-connection drop-tables))
