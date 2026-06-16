(ns janus
  [:gen-class]
  [:require [clojure.tools.cli]
   [janus.dsl]
   [janus.verify]
   [janus.text-presentation]
   [janus.support]])

(defn verify [service]
  (try
    (let [service-text (slurp service)
          service-defn (read-string service-text)
          service (janus.dsl/construct-domain service-defn)
          results [(janus.verify/verify-service service {})]]
      [0 (janus.text-presentation/display results)])
    (catch java.io.FileNotFoundException e
      [1 (str "Could not find '" service "'")])
    (catch RuntimeException e
      (do
        (. e printStackTrace)
        [1 (str "Invalid service in '" service "'. Error: " e)]))))

(defn -main [& args]
  (janus.support/environment)
  (let [{:keys [options]} (clojure.tools.cli/parse-opts
                            args
                            [["-v" "--verify SERVICE" "Service contract file to verify"]])
        service (:verify options)
        [status message] (if service
                           (verify service)
                           [0 ""])]
    (println message)
    (System/exit status)))
