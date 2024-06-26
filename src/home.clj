(ns home
  (:require [hiccup.page :refer [html5]]))

(def header
  [:header
   [:div.header-container
    [:div.website-title
     [:a {:href "/"} "Lecturian"]]
    [:nav
     [:ul
      [:li
       [:a {:href "/lectures"} "Lectures"]]
      [:li
       [:a {:href "/lessons"} "Lessons"]]
      [:li
       [:a {:href "/profile"} "Profile"]]]]]])

(defn home-page []
  (str
   (html5
    [:html
     [:head
      [:title "Home page"]
      [:link {:rel "stylesheet" :type "text/css" :href "/css/styles.css"}]
      [:link {:rel "preconnect" :href "https://fonts.googleapis.com"}]
      [:link {:rel "preconnect" :href "https://fonts.gstatic.com" :crossorigin true}]
      [:link {:href "https://fonts.googleapis.com/css2?family=Lato:ital,wght@0,100;0,300;0,400;0,700;0,900;1,100;1,300;1,400;1,700;1,900&display=swap" :rel "stylesheet"}]]
     [:body
      header
      [:div.body
       [:h1 "Learn Clojure: A Fun and Powerful Programming Language"]
       [:section
        [:article.body-text "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse at elit sed sapien eleifend mattis in in eros. Nunc tincidunt eros ex, ac malesuada tortor convallis sit amet. Suspendisse vulputate vitae ex a sodales. Curabitur venenatis congue viverra. Proin congue."]]]]])))