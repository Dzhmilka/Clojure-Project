(ns main
  (:require [ring.adapter.jetty :as jetty]
            [hiccup.core :refer [html]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defonce lessons (atom []))

;; Helper function to generate unique IDs for lessons
(defn generate-id []
  (str (java.util.UUID/randomUUID)))

;; Handler to view the list of lessons
(defn list-lessons []
  (str
   (html
    [:html
     [:head
      [:title "Lessons"]]
     [:body
      [:h1 "List of Lessons"]
      [:ul
       (for [[id lessons] @lessons]
         [:li [:a {:href (str "/lesson/" id)} (:title lessons)]])]
      [:a {:href "/new-lesson"} "Create a new lesson"]]])))

;; Handler to create a new lessons form
(defn new-lessons-form []
  (str
   (html
    [:html
     [:head
      [:title "New Lesson"]]
     [:body
      [:h1 "Create a New Lesson"]
      [:form {:action "/create-lesson" :method "post"} 
       (anti-forgery-field)
       [:p "Title: " [:input {:type "text" :name "title"}]]
       [:p "Body: " [:textarea {:name "body"}]]
       [:p [:input {:type "submit" :value "Create"}]]]]])))

;; Handler to create a new lessons
(defn create-lesson [params]
  (let [id (generate-id)
        title (get params :title)
        body (get params :body)]
    (swap! lessons assoc id {:title title :body body})
    {:status 302 :headers {"Location" (str "/lesson/" id)}}))

;; Handler to view a specific lecture
(defn view-lesson [id]
  (if-let [lesson (@lessons id)]
    (str
     (html
      [:html
       [:head
        [:title (:title lesson)]]
       [:body
        [:h1 (:title lesson)]
        [:p (:body lesson)]
        [:p [:textarea {:name "code"}]]
        [:p [:button "Run code"]]
        [:a {:href "/"} "Back to lessons"]]]))
    (str
     (html
      [:html
       [:head
        [:title "Not Found"]]
       [:body
        [:h1 "Lesson Not Found"]
        [:a {:href "/"} "Back to lessons"]]]))))

(defroutes app-routes
  (GET "/" [] (list-lessons))
  (GET "/new-lesson" [] (new-lessons-form))
  (POST "/create-lesson" {params :params}
    (create-lesson params))
  (GET "/lesson/:id" [id] (view-lesson id))
  (route/not-found "<h1>Page not found</h1>"))

(def app
  (wrap-defaults app-routes site-defaults))

(defonce server (jetty/run-jetty #'app {:port 3000 :join? false}))