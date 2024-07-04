(ns home
  (:require [hiccup.page :refer [html5]]))

(def head-links
  (list
   [:link {:rel "stylesheet" :type "text/css" :href "/css/reset.css"}]
   [:link {:rel "stylesheet" :type "text/css" :href "/css/styles.css"}]))

(def header
  [:header
   [:div.header-container
    [:div.website-title
     [:a {:href "/"} "Lecturian"]]
    [:nav
     [:ul
      [:li.header-list-item
       [:a.header-link {:href "/lectures"} "Lectures"]]
      [:li.header-list-item
       [:a.header-link {:href "/lessons"} "Lessons"]]
      [:li.header-list-item
       [:a.header-link {:href "/profile"} "Profile"]]]]]])

(defn home-page []
  (str
   (html5
    [:html
     [:head
      [:title "Home page"]
      head-links]
     [:body
      header
      [:div.main-body
       [:h1.home-title "Learn Clojure: A Fun and Powerful Programming Language"]
       [:section.home-container
         [:article.article "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse at elit sed sapien eleifend mattis in in eros. Nunc tincidunt eros ex, ac malesuada tortor convallis sit amet. Suspendisse vulputate vitae ex a sodales. Curabitur venenatis congue viverra. Proin congue."]
         [:div.body-list
          [:h3 "Check these lectures:"]
          [:ul
           [:li "Lecture 1"]
           [:li "Lecture 2"]]]]]]])))