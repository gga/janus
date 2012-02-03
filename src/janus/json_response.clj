(ns janus.json-response
  [:require [json-path]]
  [:use [clojure.data.json :only [read-json]]])

(defn extract-rule [clause]
  (first (filter #(not= :path %) (keys clause))))

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
  (if (not (re-find expected actual))
    (str "Expected \"" actual "\" to match regex " expected)))

(defn verify-document [doc clauses]
  (let [json-doc (read-json doc)]
    (filter #(not= nil %) (map (fn [clause]
                                 (let [rule (extract-rule clause)
                                       chck-sym (symbol (name rule))
                                       doc-part (json-path/at-path (:path clause) json-doc)]
                                   ((resolve chck-sym) (get clause rule) doc-part)))
                               clauses))))
