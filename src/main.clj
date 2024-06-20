(ns main
  (:require [hiccup2.core :refer [html]]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [database :as db]))

(def common-interceptors [(body-params/body-params) http/json-body])

;; Helper function to generate unique IDs for lessons
(defn generate-id []
  (str (java.util.UUID/randomUUID)))

;; Handler to view the list of lessons
(defn list-lessons [request]
  (let [lessons (db/search-lesson)]
    (println lessons)
    {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str
          (html
           [:html
            [:head
             [:title "Lessons"]]
            [:body
             [:h1 "List of Lessons"]
             [:ul
              (for [[id title] lessons]
                 [:li [:a {:href (str "/lesson/" id)} title]])]
             [:a {:href "/new-lesson"} "Create a new lesson"]]]))}))

;; Handler to create a new lessons form
(defn new-lessons-form [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str
   (html
    [:html
     [:head
      [:title "New Lesson"]]
     [:body
      [:h1 "Create a New Lesson"]
      [:form {:action "/create-lesson" :method "post"}
       [:p "Title: " [:input {:type "text" :name "title"}]]
       [:p "Body: " [:textarea {:name "body"}]]
       [:p [:input {:type "submit" :value "Create"}]]]]]))})

;; Handler to create a new lessons
(defn create-lesson [{:keys [form-params]}]
  (let [id (generate-id)
        title (get form-params :title)
        body (get form-params :body)]
    (db/create-lesson id title body)
    {:status 302 :headers {"Location" (str "/lesson/" id)}}))

;; Handler to view a specific lecture
(defn view-lesson [request]
  (let [id (get-in request [:path-params :id])]
    (if-let [[title body] (first (db/search-lesson id))]
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (str
              (html
               [:html
                [:head
                 [:title title]]
                [:body
                 [:h1 title]
                 [:p body]
                 [:p [:textarea {:name "code"}]]
                 [:p [:button "Run code"]]
                 [:a {:href "/"} "Back to lessons"]]]))}
      {:status 404
       :headers {"Content-Type" "text/html"}
       :body (str
              (html
               [:html
                [:head
                 [:title "Not Found"]]
                [:body
                 [:h1 "Lesson Not Found"]
                 [:a {:href "/"} "Back to lessons"]]]))})))
(view-lesson {:path-params {:id "f6410870-af46-49d6-91a1-0d7bdcfc8777"}})
(let [[title body] (first (db/search-lesson "f6410870-af46-49d6-91a1-0d7bdcfc8777"))]
  title)
(def routes
  (route/expand-routes
   #{["/" :get list-lessons :route-name :list-lessons]
     ["/new-lesson" :get new-lessons-form :route-name :new-lessons-form]
     ["/create-lesson" :post (conj common-interceptors create-lesson) :route-name :create-lesson]
     ["/lesson/:id" :get view-lesson :route-name :view-lesson]}))

(def service-map
  {::http/routes routes
   ::http/type :jetty
   ::http/port 3000})


(defn start []
  (http/start (http/create-server service-map)))

(defonce server (atom nil))

(defn start-dev []
  (reset! server
          (http/start (http/create-server
                       (assoc service-map
                              ::http/join? false)))))

(defn stop-dev []
  (http/stop @server))

(defn restart []
  (stop-dev)
  (start-dev))
