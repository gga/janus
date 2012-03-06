(ns janus.support)

(defn at-exit [code]
  (.addShutdownHook (Runtime/getRuntime)
                    (proxy [Thread] []
                      (run []
                        (code)))))

(defn system [cmd]
  (let [proc (.exec (Runtime/getRuntime) cmd)]
    (.waitFor proc)))

(defn- load-support [support-file]
  (let [support-ns (create-ns 'support-ns)
        support-text (slurp (.getPath support-file))]
    (binding [*ns* support-ns]
      (eval '(clojure.core/refer 'clojure.core))
      (eval '(refer 'janus.support))
      (load-string support-text))))

(defn environment []
  (let [support-files (file-seq (clojure.java.io/file "." "contracts" "support"))]
    (doseq [f support-files
            :when (re-seq #"\.clj$" (.getPath f))]
      (load-support f))))
