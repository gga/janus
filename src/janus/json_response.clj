(ns janus.json-response
  [:require [json-path]]
  [:use midje.sweet
   [clojure.data.json :only [read-json]]])

(unfinished )

(defn extract-rule [clause]
  (first (filter #(not= :path %) (keys clause))))

(fact
  (extract-rule {:path "" :equal-to "b"}) => :equal-to)

(defn equal-to [expected actual]
  (if (not= actual expected)
    (str "Expected \"" expected "\". Got \"" actual "\"")))

(fact
  (equal-to :hello :hello) => nil
  (equal-to :world :hello) => "Expected \":world\". Got \":hello\"")

(defn of-type [expected actual]
  (if (not (cond
            (string? actual) (= :string expected)
            (sequential? actual) (= :array expected)
            (map? actual) (= :object expected)
            (number? actual) (= :number expected)))
    (str "Expected \"" actual "\" to be "  (name expected))))

(fact
  (of-type :string "") => nil
  (of-type :string :b) => "Expected \":b\" to be string" 
  (of-type :array [:a :b]) => nil
  (of-type :array {:a :b} ) => "Expected \"{:a :b}\" to be array"
  (of-type :object {:a "val"}) => nil
  (of-type :object "val") => "Expected \"val\" to be object"
  (of-type :number 10) => nil
  (of-type :number 1.0) => nil
  (of-type :number "") => "Expected \"\" to be number")

(defn matching [expected actual]
  (if (not (re-find expected actual))
    (str "Expected \"" actual "\" to match regex " expected)))

(fact
  (matching #"[a-z]+" "hello") => nil
  (matching #"[a-z]+" "100") => "Expected \"100\" to match regex [a-z]+")

(defn verify-document [doc clauses]
  (let [json-doc (read-json doc)]
    (filter #(not= nil %) (map (fn [clause]
                                 (let [rule (extract-rule clause)
                                       chck-sym (symbol (name rule))
                                       doc-part (json-path/at-path (:path clause) json-doc)]
                                   ((resolve chck-sym) (get clause rule) doc-part)))
                               clauses))))

(fact
  (verify-document "\"body\"" [{:path "$", :equal-to "body"}]) => empty?)