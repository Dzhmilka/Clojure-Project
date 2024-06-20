(ns database
  (:require [datomic.api :as d]
            [schema :as sc]))

(def uri "datomic:dev://localhost:4334/datomic")

;; Connect to the database
(def conn (d/connect uri))
(sc/transact-schema conn "resources/schema.edn")

(defn create-lesson [id title body]
  (d/transact conn [{:db/id (d/tempid :db.part/user)
                     :lesson/id id
                     :lesson/title title
                     :lesson/body body}]))

(defn search-lesson 
  ([] (d/q '[:find ?id ?title
             :where
             [?e :lesson/id ?id]
             [?e :lesson/title ?title]] (d/db conn)))
  ([id] (d/q '[:find ?title ?body
               :in $ ?id
               :where 
               [?e :lesson/id ?id]
               [?e :lesson/title ?title]
               [?e :lesson/body ?body]] (d/db conn) id)))

(defn create-user [username password]
  (d/transact conn [[:db/add {:user/username username :user/password password}]]))