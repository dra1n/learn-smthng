(ns learn-smthng.views.helper
  (:require [me.raynes.laser :as l]
            [clojure.java.io :as io]))

(defn get-tmpl
  "Retruns parsed template of the provided html file"
  [path]
  (l/parse
   (slurp (io/resource path))))