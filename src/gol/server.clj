(ns gol.server
  (:require [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [hiccup.page :refer [html5 include-js include-css]]
            [hiccup.middleware :refer [wrap-base-url]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.util.response :as response]))

(defn index-page
  [request]
  (html5
    [:head
     (include-css
       "//cdnjs.cloudflare.com/ajax/libs/normalize/3.0.0/normalize.min.css"
       "/css/build/styles.css")]
    [:body
     [:div#app]
     (include-js
       "//fb.me/react-0.9.0.js"
       "/js/build/goog/base.js"
       "/js/build/gol.js")
     [:script
      {:type "text/javascript"}
      "goog.require('gol.client');"]]))

(defroutes main-routes
  (GET "/" [] index-page)
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-base-url)))
