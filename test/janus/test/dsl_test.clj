(ns janus.test.dsl-test
  [:use [janus.dsl]]
  [:require [midje.sweet :as midje]])

(midje/unfinished)

(midje/fact "should-have creates clauses as required"
            (should-have :path "path" :of-type :type) => [:clause [:path "path" :of-type :type]]
            (should-have :status 200) => [:clause [:status 200]]
            (should-have :header "Content-Type" :equal-to "application/json") => [:clause [:header "Content-Type" :equal-to "application/json"]])

(midje/fact "should-have raises an error on a malformed clause")

(midje/fact "url creates a property containing the path"
            (url "path") => [:property {:name "url" :value "path"}])

(midje/fact "method creates a property containing the method"
            (method :meth) => [:property {:name "method" :value :meth}])

(midje/fact "body creates a body object with a type and data"
  (body :json {:sample "obj"}) => [:body {:type :json :data {:sample "obj"}}]
  (body "data") => [:body {:type :string :data "data"}])

(midje/fact "body allows definition of xml"
            (nth (body :xml [:tag {:attr "value"}]) 1) => (midje/just {:type :xml :data midje/anything}))

(midje/fact "before creates a property containing the setup function"
            (before 'setup-func) => [:property {:name "before" :value 'setup-func}])

(midje/fact "after creates a property containing the teardown function"
            (after 'teardown-func) => [:property {:name "after" :value 'teardown-func}])

(midje/fact "header creates a header"
            (header "Name" "Value") => [:header {:name "Name" :value "Value"}])

(defn contract-with [check-key expected-value]
  (midje/chatty-checker [actual-contract]
                  (= expected-value (check-key (nth actual-contract 1)))))

(midje/fact "defining a contract"
            (contract "sample") => (contract-with :name "sample")
            (contract "sample" (method :post)) => (contract-with :properties [{:name "method" :value :post}])
            (contract "sample" (should-have :path "$" :of-type :string)) => (contract-with :clauses [[:path "$" :of-type :string]])
            (contract "sample" (header "CT" "json")) => (contract-with :headers [{:name "CT" :value "json"}])
            (contract "sample" (body "data")) => (contract-with :body {:type :string :data "data"}))

(midje/fact "defining a service"
            (:name (service "sample")) => "sample"
            (:properties (service "sample" (method :post))) => (midje/contains {:name "method" :value :post})
            (:headers (service "sample" (header "ct" "json"))) => (midje/contains {:name "ct" :value "json"})
            (:contracts (service "sample" (contract "contract 1"))) => (midje/contains {:name "contract 1" :properties [] :headers [] :clauses [] :body nil}))

(midje/fact "loading a DSL program"
            (construct-domain '(service "sample")) => {:name "sample", :properties (), :headers (), :contracts ()})


