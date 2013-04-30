(ns learn-smthng.handler
  (:use compojure.core)
  (:require [org.httpkit.server :as server]
            [compojure.route :as route]
            [learn-smthng.authenticate.oauth :as oauth]
            [learn-smthng.views.home :as home]))

(def env (into {} (System/getenv)))

(defroutes auth-routes
  (GET "/github" [] (oauth/login
                        { :authorization-url "https://github.com/login/oauth/authorize" 
                          :token-url "https://github.com/login/oauth/access_token"
                          :client-id (env "GITHUB_CLIENT_ID") 
                          :client-secret (env "GITHUB_CLIENT_SECRET") }
                        "http://localhost/api/callback"))
  (GET "/callback" [] "Hello from github"))

(defroutes app
  (GET "/" [] (home/render-greetings))
  (context "/authorize" [] auth-routes)
  (route/resources "/")
  (route/not-found "Not Found"))

(defn -main [& args]
  (server/run-server app { :ip (env "HOST") :port (Integer/parseInt (env "PORT")) }))
