;; Require and use list is very strict here as only symbols that are
;; safe to use in a contract definition should be available.
(ns janus.dsl
  [:require [midje.sweet :as midje]])

(midje/unfinished)

(defmulti should-have (fn [& args] (nth args 0)))
(defmethod should-have :path [& args]
  [:clause [:path (nth args 1) (nth args 2) (nth args 3)]])
(defmethod should-have :status [& args]
  [:clause [:status (nth args 1)]])
(defmethod should-have :header [& args]
  [:clause [:header (nth args 1) (nth args 2) (nth args 3)]])

(midje/fact "should-have creates clauses as required"
            (should-have :path "path" :of-type :type) => [:clause [:path "path" :of-type :type]]
            (should-have :status 200) => [:clause [:status 200]]
            (should-have :header "Content-Type" :equal-to "application/json") => [:clause [:header "Content-Type" :equal-to "application/json"]])

(midje/fact "should-have raises an error on a malformed clause")

(defn url [path]
  [:property {:name "url" :value path}])

(midje/fact "url creates a property containing the path"
            (url "path") => [:property {:name "url" :value "path"}])

(defn method [kw-method]
  [:property {:name "method" :value kw-method}])

(midje/fact "method creates a property containing the method"
            (method :meth) => [:property {:name "method" :value :meth}])

(defn before [setup-func]
  [:property {:name "before" :value setup-func}])

(midje/fact "before creates a property containing the setup function"
            (before 'setup-func) => [:property {:name "before" :value 'setup-func}])

(defn after [teardown-func]
  [:property {:name "after" :value teardown-func}])

(midje/fact "after creates a property containing the teardown function"
            (after 'teardown-func) => [:property {:name "after" :value 'teardown-func}])

(defn header [name value]
  [:header {:name name :value value}])

(midje/fact "header creates a header"
            (header "Name" "Value") => [:header {:name "Name" :value "Value"}])

(defn contract [name & definition]
  [:contract {:name name
              :properties (map #(nth % 1) (filter #(= :property (first %)) definition))
              :clauses (map #(nth % 1) (filter #(= :clause (first %)) definition))
              :headers (map #(nth % 1) (filter #(= :header (first %)) definition))}])

(defn contract-with [check-key expected-value]
  (midje/chatty-checker [actual-contract]
                  (= expected-value (check-key (nth actual-contract 1)))))

(midje/fact "defining a contract"
            (contract "sample") => (contract-with :name "sample")
            (contract "sample" (method :post)) => (contract-with :properties [{:name "method" :value :post}])
            (contract "sample" (should-have :path "$" :of-type :string)) => (contract-with :clauses [[:path "$" :of-type :string]])
            (contract "sample" (header "CT" "json")) => (contract-with :headers [{:name "CT" :value "json"}]))

(defn service [name & definition]
  {:name name
   :properties (map #(nth % 1) (filter #(= :property (first %)) definition))
   :headers (map #(nth % 1) (filter #(= :header (first %)) definition))
   :contracts (map #(nth % 1) (filter #(= :contract (first %)) definition))})

(midje/fact "defining a service"
            (:name (service "sample")) => "sample"
            (:properties (service "sample" (method :post))) => (midje/contains {:name "method" :value :post})
            (:headers (service "sample" (header "ct" "json"))) => (midje/contains {:name "ct" :value "json"})
            (:contracts (service "sample" (contract "contract 1"))) => (midje/contains {:name "contract 1" :properties [] :headers [] :clauses []}))
