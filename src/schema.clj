(ns schema
  (:require [datomic.api :as d]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn load-schema [file]
  (with-open [r (io/reader file)]
    (try
      (edn/read (java.io.PushbackReader. r))
      (catch Throwable e
        (throw (ex-info "Error reading schema file" {:file file :cause e}))))))

(defn transact-schema [conn schema-file]
  (let [schema (load-schema schema-file)]
    (try
      (d/transact conn schema)
      (catch Exception e
        (throw (ex-info "Error during schema transaction" {:file schema-file :cause e}))))))