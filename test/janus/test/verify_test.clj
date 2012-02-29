(ns janus.test.verify-test
  [:use janus.verify
   midje.sweet]
  [:require [clj-http.client :as http]])

(unfinished )

(fact
  (extract-clause :s ..contract.. ..context..) => [[:s 1] [:s 2]]
  (provided
    ..contract.. =contains=> {:clauses [[:s 1]]}
    ..context.. =contains=> {:clauses [[:s 2]]}))

(fact "a status clause should check against a response correctly"
  (check-clause {:status 200} [:status 201]) => "Expected status 201. Got status 200")

(fact "a header clause should allow equality and matching checks"
  (check-clause {:headers {"ct" "blah"}} [:header "ct" :equal-to "blah"]) => empty?
  (check-clause {:headers {"ct" "foo"}} [:header "ct" :equal-to "bar"]) => "Expected header 'ct' to equal 'bar'. Got 'foo'.")

(against-background [..response.. =contains=> {:status 200}
                     ..response.. =contains=> {:headers {"ct" "app/json"}}
                     ..response.. =contains=> {:body "example body"}]
  (fact "reponse with different status fails"
    (errors-in-envelope ..response.. ..contract.. ..context..) => ["Expected status 201. Got status 200"]
    (provided
      ..contract.. =contains=> {:clauses [[:status 201]]})
    (errors-in-envelope ..response.. ..contract.. ..context..) => ["Expected header 'ct' to equal 'text/html'. Got 'app/json'."]
    (provided
      ..contract.. =contains=> {:clauses [[:header "ct" :equal-to "text/html"]]})))

(fact "the body is checked depending on the content-type of document received"
  (against-background)
  
  (errors-in-body ..response.. ..contract.. ..context..) => empty?
  (provided
    ..response.. =contains=> {:headers {"content-type" "application/json"}}
    (janus.json-response/verify-document anything anything) => [])

  (errors-in-body ..response.. ..contract.. ..context..) => empty?
  (provided
    ..response.. =contains=> {:headers {"content-type" "application/vnd.example.account+json"}}
    (janus.json-response/verify-document anything anything) => []))

(against-background [..contract.. =contains=> {:properties []}
                     ..context.. =contains=> {:properties []}]
  (fact "property values are extracted from the contract"
    (property "prop" ..contract.. ..context..) => "contract val"
    (provided
      ..contract.. =contains=> {:properties [{:name "prop" :value "contract val"}]})
    
    (property "prop" ..contract.. ..context..) => "first"
    (provided
      ..contract.. =contains=> {:properties [{:name "prop" :value "first"}
                                             {:name "prop" :value "second"}]})

    (property "prop" ..contract.. ..context..) => nil)

  (fact "property values are extracted from the context"
    (property "prop" ..contract.. ..context..) => "context val"
    (provided
      ..context.. =contains=> {:properties [{:name "prop" :value "context val"}]})))

(against-background
  [(http/request {:method :get, :url "url"}) => "http response"
   ..contract.. =contains=> {:name "sample contract"}
   (property "method" ..contract.. ..context..) => :get
   (property "url" ..contract.. ..context..) => "url"
   (errors-in-envelope "http response" ..contract.. ..context..) => []
   (errors-in-body "http response" ..contract.. ..context..) => []]
  
  (fact "a valid service succeeds"  
    (verify-contract ..contract.. ..context..) => ["sample contract" :succeeded])

  (fact "a service with an invalid envelope provides descriptive messages"
    (verify-contract ..contract.. ..context..) => ["sample contract" :failed
                                                   ["Expected status to be: 201. Got: 200"]]
    (provided
      (errors-in-envelope "http response" ..contract.. ..context..) => ["Expected status to be: 201. Got: 200"]))

  (fact "a service with an invalid body provides descriptive messages"
    (verify-contract ..contract.. ..context..) => ["sample contract" :failed
                                                   ["Expected body to match."]]
    (provided
      (errors-in-body "http response" ..contract.. ..context..) => ["Expected body to match."])))

(facts
  (verify-service ..service.. ..context..) => [["sample" :succeeded]]
  (provided
    ..service.. =contains=> {:contracts [{:name "sample"}]}
    (verify-contract {:name "sample"} ..context..) => ["sample" :succeeded])

  (verify-service ..service.. ..context..) => [["sample" :failed ["message"]]]
  (provided
    ..service.. =contains=> {:contracts [{:name "sample"}]}
    (verify-contract {:name "sample"} ..context..) => ["sample" :failed ["message"]]))
