(defproject learn-smthng "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.postgresql/postgresql "9.2-1002-jdbc4"]
                 [me.raynes/laser "1.1.1"]
                 [korma "0.3.0-RC5"]
                 [http-kit "2.0.0"]
                 [drift "1.4.5"]
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
