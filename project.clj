(defproject ficetranslation "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.6.0"] [org.clojure/data.csv "0.1.2"]]
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :aot [ficetranslation.core]
  :main ficetranslation.core)
