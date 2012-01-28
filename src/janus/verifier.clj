(ns janus.verifier
  [:require [clj-http.client :as http]
   [janus.json-response]
   [json-path]]
  [:use midje.sweet
   clj-http.fake])

(unfinished )

(defn extract-clause [clause contract context]
  (filter #(= clause (nth % 0))
          (concat (:clauses contract) (:clauses context))))

(fact
  (extract-clause :s ..contract.. ..context..) => [[:s 1] [:s 2]]
  (provided
    ..contract.. =contains=> {:clauses [[:s 1]]}
    ..context.. =contains=> {:clauses [[:s 2]]}))

(defmulti check-clause (fn [response clause] (nth clause 0)))

(defmethod check-clause :status [response clause]
  (let [actual (:status response)
        expected (nth clause 1)]
    (if (not= expected actual)
      (str "Expected status " expected ". Got status " actual))))

(fact "a status clause should check against a response correctly"
  (check-clause {:status 200} [:status 201]) => "Expected status 201. Got status 200")

(defmethod check-clause :header [response clause]
  (let [[_ header-name comparison expected] clause
        actual (-> response :headers (get header-name))]
    (cond
     (= :equal-to comparison) (if (not= expected actual)
                               (str "Expected header '" header-name "' to equal '" expected "'. Got '" actual "'.")))))

(fact "a header clause should allow equality and matching checks"
  (check-clause {:headers {"ct" "blah"}} [:header "ct" :equal-to "blah"]) => empty?
  (check-clause {:headers {"ct" "foo"}} [:header "ct" :equal-to "bar"]) => "Expected header 'ct' to equal 'bar'. Got 'foo'.")

(defn errors-in-envelope [response contract context]
  (concat
   (map (partial check-clause response)
        (extract-clause :status contract context))
   (map (partial check-clause response)
        (extract-clause :header contract context))))

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

(defn errors-in-body [response contract context]
  (let [doc-type (-> response :headers (get "Content-Type"))
        body (:body response)
        clauses (concat (:clauses contract) (:clauses context))]
    (cond
     (or (= "application/json" doc-type)
         (re-seq #"\+json" doc-type)) (janus.json-response/verify-document body clauses))))

(fact "the body is checked depending on the content-type of document received"
  (against-background)
  
  (errors-in-body ..response.. ..contract.. ..context..) => empty?
  (provided
    ..response.. =contains=> {:headers {"Content-Type" "application/json"}}
    (janus.json-response/verify-document anything anything) => [])

  (errors-in-body ..response.. ..contract.. ..context..) => empty?
  (provided
    ..response.. =contains=> {:headers {"Content-Type" "application/vnd.example.account+json"}}
    (janus.json-response/verify-document anything anything) => []))

(defn property [prop-name contract context]
  (:value (first (filter #(= prop-name (:name %))
                         (concat (:properties contract) (:properties context))))))

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

(defn verify-contract [contract context]
  (let [response (http/request {:method (property "method" contract context),
                                :url (property "url" contract context)})
        envelope-errors (errors-in-envelope response ..contract.. ..context..)
        body-errors (errors-in-body response ..contract.. ..context..)
        errors (concat envelope-errors body-errors)]
    (if (empty? errors)
      [(:name contract) :succeeded]
      [(:name contract) :failed errors])))

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
