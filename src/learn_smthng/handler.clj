(ns learn-smthng.handler
  (:use compojure.core)
  (:require [org.httpkit.server :as server]
            [compojure.route :as route]))

(def env (into {} (System/getenv)))

(defroutes app
  (GET "/" [] "Hello World")
  (route/resources "/")
  (route/not-found "Not Found"))

(defn -main [& args]
  (server/run-server app { :ip (env "HOST") :port (Integer/parseInt (env "PORT")) }))
