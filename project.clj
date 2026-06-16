(defproject janus "0.0.2"
  :description "Consumer-driven contracts, verified both ways."
  :url "http://github.com/gga/janus"
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [org.clojure/data.json "2.5.0"]
                 [json-path "2.2.0"]
                 [clj-http "3.13.0"]
                 [org.clojure/core.logic "1.0.1"]
                 [org.clojure/tools.cli "1.4.256"]
                 [org.clojure/data.xml "0.0.8"]]
  :profiles {:dev {:dependencies [[clj-http-fake "1.0.4"]
                                  [midje "1.10.9"]]
                   :plugins [[lein-midje "3.2.2"]]}
             :uberjar {:aot :all}}
  :main janus)
