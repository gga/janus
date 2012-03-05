(ns janus.support)

(defn at-exit [code]
  (.addShutdownHook (Runtime/getRuntime)
                    (proxy [Thread] []
                      (run []
                        (code)))))

(defn- load-support [support-file]
  (let [support-ns (create-ns 'support-ns)]
    (binding [*ns* support-ns]
      (eval '(clojure.core/refer 'clojure.core))
      (eval '(refer 'janus.support))
      (eval (read-string (slurp (.getPath support-file)))))))

(defn environment []
  (let [support-files (file-seq (clojure.java.io/file "." "contracts" "support"))]
    (doseq [f support-files
            :when (re-seq #"\.clj$" (.getPath f))]
      (load-support f))))
