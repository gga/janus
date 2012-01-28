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
  {:name name
   :properties (map #(nth % 1) (filter #(= :property (first %)) definition))
   :clauses (map #(nth % 1) (filter #(= :clause (first %)) definition))
   :headers (map #(nth % 1) (filter #(= :header (first %)) definition))})

(midje/fact "defining a contract"
            (:name (contract "sample")) => "sample"
            (:properties (contract "sample" (method :post))) => (midje/contains {:name "method" :value :post})
            (:clauses (contract "sample" (should-have :path "$" :of-type :string))) => '([:path "$" :of-type :string])
            (:headers (contract "sample" (header "CT" "json"))) => (midje/contains  {:name "CT" :value "json"}))