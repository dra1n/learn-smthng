(ns learn-smthng.views.home
  (:require [me.raynes.laser :as l]
            [learn-smthng.views.helper :as h]
            [clojure.java.io :as io]))

;; Templates
(def layout    (h/get-tmpl "public/html/layout.html"))
(def home-page (h/get-tmpl "public/html/home.html"))

(defn render-greetings
  []
  (l/document layout
    (l/id= "content")
    (l/content home-page)))