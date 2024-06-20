(ns home
  (:require [hiccup2.core :refer [html]]
            [io.pedestal.http :as http]))

(defn main-page [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str
          (html
           [:html
            [:head
             [:title "Home page"]]
            [:body
             ]]))})