(defproject janus "1.0.0-SNAPSHOT"
  :description "Consumer-driven contracts, verified both ways."
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/data.json "0.1.1"]
                 [clj-http "0.2.4"]]
  :dev-dependencies [[swank-clojure "1.4.0-SNAPSHOT"]
                     [clj-http-fake "0.2.3"]
                     [midje "1.3-alpha5"]
                     [lein-midje "1.0.4"]])