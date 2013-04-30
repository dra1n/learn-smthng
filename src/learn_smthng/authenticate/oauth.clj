(ns learn-smthng.authenticate.oauth
  "OAuth authentication"
  (:require
   [cheshire.core :as json]
   [clj-http.client :as http]
   [clojure.string :as string]
   [clojure.tools.logging :as logging])
  (:use
   [oauthentic.core :only [build-authorization-url fetch-token]]
   [ring.util.response :only [redirect]]
;   [myapp.user :only [add-user update-user user-from-token]]
   [slingshot.slingshot :only [throw+]]))
 
(def github-endpoint "https://api.github.com")
 
(defn github-user-lookup
  [token]
  (let [resp (http/get (str github-endpoint "/user")
                  {:headers {"Authorization" (str "token " token)}})
        parser #(json/parse-string % true)
        data (-> resp :body parser)]
    (merge
     (select-keys data [:email :company])
     {:username (:login data)
      :fullname (:name data)})))
 
(defn github-auth
  "GitHub authentication endpoint.
  See http://developer.github.com/v3/oauth.
 
  `client-id`
  : The client ID you received from GitHub when you registered your application
 
  `scopes`
  : A sequence of scope strings (scopes are described here:
    http://developer.github.com/v3/oauth/#scopes)"
  [client-id client-secret scopes]
  {:type :oauth
   :authority "github"
   :params {:authorization-url "https://github.com/login/oauth/authorize"
            :authorization-params (fn [redirect-url]
                                    {:client_id client-id
                                     :scope (string/join "," scopes)
                                     :redirect-uri redirect-url})
            :access-token-url "https://github.com/login/oauth/access_token"
            :access-token-params (fn [code]
                                   {:client-id client-id
                                    :client-secret client-secret
                                    :code code})}
   :user-lookup github-user-lookup})
 
(defn authentication-url
  "Authenticate via OAuth"
  [{:keys [params] :as auth-option} redirect-uri]
  (let [{:keys [authorization-url authorization-params]} params]
    (build-authorization-url
     authorization-url (authorization-params redirect-uri))))

(defn authentication-token
  [{:keys [params] :as auth-option} redirect-uri {:as request}]
  (logging/debugf "authentication-token params %s" (:params request))
  (let [{:keys [access-token-url access-token-params access-token-url]} params
        code (-> request :params (get "code"))
        error (-> request :params (get "error"))]
    (if error
      (throw+
       {:reason :oauth-failed
        :error error}
       "OAuth failed %s" error)
      (do
        (logging/debugf
         "authentication-token %s %s"
         access-token-url
         (access-token-params code))
        (let [{:keys [access-token token-type]} (fetch-token
                                                 access-token-url
                                                 (access-token-params code))]
          access-token)))))
 
(defn login
  "Returns a function that redirects a user for authentication."
  [params redirect-url]
  (fn [request]
    (let [url (authentication-url params redirect-url)]
      (logging/debugf "Redirect to %s for OAuth login" url)
      (redirect url))))

(login
                        { :authorization-url "https://github.com/login/oauth/authorize" 
                          :token-url "https://github.com/login/oauth/access_token"
                          :client-id "ecb68c336148102a2955" 
                          :client-secret "e590760e65e233f620f2ed7bcc43d9a8be1ccfa4" }
                        "http://localhost/api/callback") {:body "rofl"}

(authentication-url { :client-id "INSERT YOUR OWN ID" :redirect-uri "http://yoursite.com/oauth/endpoint" } "http://google.com")
 
;(defn update-user-from-authority
;  [user user-data]
;  (let [empty-fields (map key (filter (comp nil? val) user))
;        new-data (into {} (filter val (select-keys user-data empty-fields)))]
;    (if (seq new-data)
;      (update-user (merge user new-data))
;      user)))
 
(defn authenticate
  "Returns a function that returns an authenticated user."
  [{:keys [authority user-lookup] :as auth-option} redirect-url]
  (fn [request]
    (try
      (let [token (authentication-token auth-option redirect-url request)]
        ;(let [user (user-from-token token authority)
        (let [user nil
              user-data (user-lookup token)]
          (logging/debugf "authenticate %s %s" user user-data)
          (if user
            ;(update-user-from-authority user user-data)
            ;(let [user (add-user user-data authority token)]
            (let [user nil]
              user))))
      (catch Exception e
        (logging/warnf e "Couldn't get token for user")))))
 
(defn oauth-workflow
  "OAuth workflow for friend"
  [& {:keys [oauth-params login-url redirect-url]}]
  (let [login (login oauth-params redirect-url)
        authenticate (authenticate oauth-params redirect-url)]
    (fn [{:keys [uri] :as request}]
      (logging/debugf "oauth-workflow %s" uri)
      (cond
        (= uri (.getPath login-url))
        (login request)
 
        (= uri (.getPath redirect-url))
        (let [user (authenticate request)]
          (if user
            (vary-meta
             {:identity (:id user) :roles #{:user}}
             merge
             {:cemerick.friend/workflow :oauth
              :type :cemerick.friend/auth})
            (logging/warn "AUTH FAIL - should never happen")))))))