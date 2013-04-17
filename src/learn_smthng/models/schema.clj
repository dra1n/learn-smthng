(ns learn-smthng.models.schema)

(def env (into {} (System/getenv)))

(def dbhost     (get env "DB_HOST"))
(def dbport     (get env "DB_PORT"))
(def dbname     (get env "DB_NAME"))
(def dbuser     (get env "DB_USERNAME"))
(def dbpassword (get env "DB_PASSWORD"))

(def db-spec {:db dbname
              :user dbuser
              :password dbpassword
              :host dbhost
              :port dbport
              :delimiters ""})
