(ns janus.json-response
  [:require [json-path]]
  [:use [clojure.data.json :only [read-json]]])

(defn extract-rule [clause]
  (nth clause 2))

(defn equal-to [expected actual]
  (if (not= actual expected)
    (str "Expected \"" expected "\". Got \"" actual "\"")))

(defn of-type [expected actual]
  (if (not (cond
            (string? actual) (= :string expected)
            (sequential? actual) (= :array expected)
            (map? actual) (= :object expected)
            (number? actual) (= :number expected)))
    (str "Expected \"" actual "\" to be "  (name expected))))

(defn matching [expected actual]
  (if (not (re-find expected (str actual)))
    (str "Expected \"" actual "\" to match regex " expected)))

(defn check [rule expected actual]
  (cond
   (= :equal-to rule) (equal-to expected actual)
   (= :of-type rule) (of-type expected actual)
   (= :matching rule) (matching expected actual)))

(defn verify-clause [value clause]
  (let [rule (extract-rule clause)
        failure (check rule (nth clause 3) value)]
    (if failure
      (str failure ", at path " (nth clause 1)))))

(defn verify-seq [actual-seq clause]
  (filter #(not= nil %) (map #(verify-clause % clause) actual-seq)))

(defn verify-document [doc clauses]
  (let [json-doc (read-json doc)]
    (flatten (filter #(not= nil %)
                     (map (fn [clause]
                            (let [doc-part (json-path/at-path (nth clause 1) json-doc)]
                              (if (sequential? doc-part)
                                (verify-seq doc-part clause)
                                (verify-clause doc-part clause))))
                          clauses)))))
