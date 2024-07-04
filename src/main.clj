(ns main
  (:require [hiccup.util :refer [raw-string]]
            [hiccup.page :refer [html5]]
            [compojure.core :refer [GET POST defroutes]]
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
        [:section.main-body
         [:h1 "List of Lessons"]
         [:ul
          (for [[id title] lessons]
            [:li [:a {:href (str "/lesson/" id)} title]])]
         [:a {:href "/new-lesson"} "Create a new lesson"]]]]))))

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
      [:section.main-body
       [:h1 "Create a New Lesson"]
       [:form {:action "/create-lesson" :method "post"}
        (raw-string (anti-forgery-field))
        [:p "Title: " [:input {:type "text" :name "title"}]]
        [:p "Body: " [:textarea {:name "body"}]]
        [:p [:input {:type "submit" :value "Create"}]]]]]])))

;; Handler to create a new lessons
(defn create-lesson [form-params]
  (let [id (generate-id)
        title (get form-params :title)
        body (get form-params :body)]
    (log/info "Parametrs recieved: " form-params title body)
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
        [:div.main-body
         [:h1.lesson-title title]
         [:section.lesson-container 
          [:article.lesson-info 
           [:p body]
           [:a {:href "/lessons"} "Back to lessons"]]
          [:article.lesson-code
           [:p [:textarea {:name "code"}]]
           [:p [:button "Run code"]]]]]]]))
    (str
     (html5
      [:html
       [:head
        [:title "Not Found"]
        head-links]
       [:body
        header
        [:div.main-body
         [:h1 "Lesson Not Found"]
         [:a {:href "/lessons"} "Back to lessons"]]]]))))

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
