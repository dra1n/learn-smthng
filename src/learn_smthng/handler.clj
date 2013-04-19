(ns learn-smthng.handler
  (:use compojure.core)
  (:require [org.httpkit.server :as server]
            [compojure.route :as route]
            [learn-smthng.views.home :as home]))

(def env (into {} (System/getenv)))

(defroutes app
  (GET "/" [] (home/render-greetings))
  (route/resources "/")
  (route/not-found "Not Found"))

(defn -main [& args]
  (server/run-server app { :ip (env "HOST") :port (Integer/parseInt (env "PORT")) }))
