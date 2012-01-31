(ns janus
  [:gen-class]
  [:require [clojure.tools.cli]
   [janus.dsl]
   [janus.verify]])

(defn verify [service]
  (try
    (let [service-text (slurp service)
          service-defn (read-string service-text)
          service (janus.dsl/construct-domain service-defn)]
      (println service)
      [0 "Successful"])
    (catch java.io.FileNotFoundException e
      [1 (str "Could not find '" service "'")])
    (catch RuntimeException e
      [1 (str "Invalid service in '" service "'.")])))

(defn -main [& args]
  (let [config (clojure.tools.cli/cli args
                                      ["-v" "--verify" "Services to verify"])
        service (:verify (nth config 0))
        [status message] (cond
                          service (verify service)
                          :else [0 ""])]
    (do (println message)
        (System/exit status))))
