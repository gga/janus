(ns janus.support)

(defn- load-support [support-file]
  (eval (read-string (slurp (.getPath support-file)))))

(defn environment []
  (let [support-files (file-seq (clojure.java.io/file "." "contracts" "support"))]
    (doseq [f support-files
            :when (re-seq #"\.clj$" (.getPath f))]
      (load-support f))))
