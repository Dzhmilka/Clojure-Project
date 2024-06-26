(ns main
  (:require [hiccup2.core :refer [html]]
            [hiccup.util :refer [raw-string]]
            [hiccup.page :refer [html5]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.adapter.jetty :as jetty]
            [database :as db]
            [clojure.tools.logging :as log]
            [home :refer [home-page]]))

;; Helper function to generate unique IDs for lessons
(defn generate-id []
  (str (java.util.UUID/randomUUID)))

(def head-links
  (list
   [:link {:rel "stylesheet" :type "text/css" :href "/css/styles.css"}]
   [:link {:rel "preconnect" :href "https://fonts.googleapis.com"}]
   [:link {:rel "preconnect" :href "https://fonts.gstatic.com" :crossorigin true}]
   [:link {:href "https://fonts.googleapis.com/css2?family=Lato:ital,wght@0,100;0,300;0,400;0,700;0,900;1,100;1,300;1,400;1,700;1,900&display=swap" :rel "stylesheet"}]))

(def header
  [:header
   [:div.header-container
    [:div.website-title 
     [:a.header-link {:href "/"} "Lecturian"]]
    [:nav
     [:ul
      [:li
       [:a.header-link {:href "/lectures"} "Lectures"]]
      [:li
       [:a.header-link {:href "/lessons"} "Lessons"]]
      [:li
       [:a.header-link {:href "/profile"} "Profile"]]]]]])

;; Handler to view the list of lessons
(defn list-lessons []
  (let [lessons (db/search-lesson)]
    (str
     (html5
      [:html
       [:head
        [:title "Lessons"]
        head-links]
       [:body
        header
        [:h1 "List of Lessons"]
        [:ul
         (for [[id title] lessons]
           [:li [:a {:href (str "/lesson/" id)} title]])]
        [:a {:href "/new-lesson"} "Create a new lesson"]]]))))

;; Handler to create a new lessons form
(defn new-lessons-form []
  (str
   (html5
    [:html
     [:head
      [:title "New Lesson"]
      head-links]
     [:body
      header
      [:h1 "Create a New Lesson"]
      [:form {:action "/create-lesson" :method "post"}
       (raw-string (anti-forgery-field))
       [:p "Title: " [:input {:type "text" :name "title"}]]
       [:p "Body: " [:textarea {:name "body"}]]
       [:p [:input {:type "submit" :value "Create"}]]]]])))

;; Handler to create a new lessons
(defn create-lesson [form-params]
  (let [id (generate-id)
        title (get form-params :title)
        body (get form-params :body)]
    (log/info "Parametrs got: " form-params title body)
    (db/create-lesson id title body)
    {:status 302 :headers {"Location" (str "/lesson/" id)}}))

;; Handler to view a specific lecture
(defn view-lesson [id]
  (if-let [[title body] (first (db/search-lesson id))]
    (str
     (html5
      [:html
       [:head
        [:title title]
        head-links]
       [:body
        header
        [:h1 title]
        [:p body]
        [:p [:textarea {:name "code"}]]
        [:p [:button "Run code"]]
        [:a {:href "/"} "Back to lessons"]]]))
    (str
     (html5
      [:html
       [:head
        [:title "Not Found"]
        head-links]
       [:body
        [:h1 "Lesson Not Found"]
        [:a {:href "/"} "Back to lessons"]]]))))

(defroutes app-routes
  (GET "/" [] (home-page))
  (GET "/lessons" [] (list-lessons))
  (GET "/new-lesson" [] (new-lessons-form))
  (POST "/create-lesson" {params :params}
    (log/info "Parameters passed: " params)
    (create-lesson params))
  (GET "/lesson/:id" [id] (view-lesson id))
  (route/resources "/public/")
  (route/not-found "<h1>Page not found</h1>"))

(def app
  (wrap-defaults app-routes site-defaults))

(defonce server (jetty/run-jetty #'app {:port 3000 :join? false}))
