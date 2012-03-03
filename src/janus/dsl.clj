(ns janus.dsl)

(defmulti should-have (fn [& args] (nth args 0)))
(defmethod should-have :path [& args]
  [:clause [:path (nth args 1) (nth args 2) (nth args 3)]])
(defmethod should-have :status [& args]
  [:clause [:status (nth args 1)]])
(defmethod should-have :header [& args]
  [:clause [:header (nth args 1) (nth args 2) (nth args 3)]])

(defn url [path]
  [:property {:name "url" :value path}])

(defn method [kw-method]
  [:property {:name "method" :value kw-method}])

(defn body [& args]
  [:body
   (if (keyword? (nth args 0))
     {:type (nth args 0) :data (nth args 1)}
     {:type :string :data (nth args 0)})])

(defn before [setup-func]
  [:property {:name "before" :value setup-func}])

(defn after [teardown-func]
  [:property {:name "after" :value teardown-func}])

(defn header [name value]
  [:header {:name name :value value}])

(defn contract [name & definition]
  [:contract {:name name
              :properties (map #(nth % 1) (filter #(= :property (first %)) definition))
              :clauses (map #(nth % 1) (filter #(= :clause (first %)) definition))
              :headers (map #(nth % 1) (filter #(= :header (first %)) definition))
              :body (first (map #(nth % 1) (filter #(= :body (first %)) definition)))}])

(defn service [name & definition]
  {:name name
   :properties (map #(nth % 1) (filter #(= :property (first %)) definition))
   :headers (map #(nth % 1) (filter #(= :header (first %)) definition))
   :contracts (map #(nth % 1) (filter #(= :contract (first %)) definition))})

(defn construct-domain [dsl-form]
  (let [dsl-ns (create-ns 'dsl-defn)
        compiled (binding [*ns* dsl-ns]
                   (eval '(clojure.core/refer 'clojure.core))
                   (eval '(refer 'janus.dsl))
                   (eval dsl-form))]
    (remove-ns 'dsl-defn)
    compiled))
