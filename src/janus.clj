(ns janus
  [:gen-class]
  [:require [clojure.tools.cli]]
  [:use midje.sweet])

(defn verify [contract]
  (try
    (let [contract-text (slurp contract)
          contract-defn (read-string contract-text)]
      (println contract-defn))
    (catch java.io.FileNotFoundException e
      [1 (str "Could not find '" contract "'")])
    (catch RuntimeException e
      [1 (str "Invalid contract in '" contract "'")])))

(defn -main [& args]
  (let [config (clojure.tools.cli/cli args
                                      ["-v" "--verify" "Contracts to verify"])
        contract (:verify (nth config 0))
        [status message] (cond
                          contract (verify contract))]
    (do (println message)
        status)))
