(defproject learn-smthng "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 
                 ; Database adapter
                 [org.postgresql/postgresql "9.2-1002-jdbc4"]
                 [korma "0.3.0-RC5"]
                 
                 ; Stuff required for authentication handling
                 [cheshire "5.1.1"]
                 [slingshot "0.10.3"]
                 [oauthentic "1.0.1"]
                 [clj-http "0.1.1"]
                 
                 ; html generation
                 [me.raynes/laser "1.1.1"]
                 
                 ; Lightweight http server
                 [http-kit "2.0.0"]
                 
                 ; Migrations FTW!
                 [drift "1.4.5"]
                 
                 ; Web stuff
                 [compojure "1.1.5"]]
  
  :plugins [[lein-ring "0.8.2"]
            [drift "1.4.5"]]
  
  :main learn-smthng.handler
  
  :ring {:handler learn-smthng.handler/app
         :auto-reload? true
         :auto-refresh? true
         :reload-paths ["src" "resources"]}
  
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]
                        [drift "1.4.5"]]}})
