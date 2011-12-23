(defproject janus "0.0.1"
  :description "Consumer-driven contracts, verified both ways."
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/data.json "0.1.1"]
                 [json-path "0.2.0"]
                 [clj-http "0.2.4"]
                 [org.clojure/core.logic "0.6.6"]]
  :dev-dependencies [[swank-clojure "1.4.0-SNAPSHOT"]
                     [clj-http-fake "0.2.3"]
                     [midje "1.3.1"]
                     [lein-midje "1.0.4"]])