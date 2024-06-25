(ns home
  (:require [hiccup2.core :refer [html]]
            [main :refer [header]]))

(defn home-page []
  (str
   (html
    [:html
     [:head
      [:title "Home page"]
      [:link {:rel "stylesheet" :type "text/css" :href "/css/styles.css"}]]
     [:body
      (header)]])))