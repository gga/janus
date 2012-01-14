(ns janus.verifier
  [:require [clj-http.client :as http]
   [janus.json-response]
   [json-path]]
  [:use midje.sweet
   clj-http.fake])

(unfinished )

(defn verify-response [response field validation]
  (if (not= (get response field) (get validation field))
    (str "Expected " (name field) ": " (get validation field) ". "
         "Actual "  (name field) ": " (get response field))))

(fact ""
  (verify-response ..resp.. :status ..validation..) => nil?
  (provided
    ..resp.. =contains=> {:status 200}
    ..validation.. =contains=> {:status 200})
  (verify-response ..resp.. :status ..validation..) => "Expected status: 200. Actual status: 201"
  (provided
    ..resp.. =contains=> {:status 201}
    ..validation.. =contains=> {:status 200}))

(defn verify-service [service]
  (let [response (http/request {:method (:method service), :url (:url service)})
        body (:body response)]
    {:envelope (filter #(not (nil? %)) 
                       (map #(verify-response response % service) [:status]))
     :contents (janus.json-response/verify-document body (:clauses service))}))

(with-fake-routes
  {"http://example.com/" (fn [req] {:status 201,
                                    :headers {},
                                    :body (str "\"body via " (name (:request-method req)) "\"")})}
  
  (fact "should verify status codes"
    (:envelope (verify-service ..service..)) => (contains "Expected status: 200. Actual status: 201")
    (provided
      (janus.json-response/verify-document anything []) => nil
      ..service.. =contains=> {:method :get}
      ..service.. =contains=> {:url "http://example.com/"}
      ..service.. =contains=> {:status 200}
      ..service.. =contains=> {:clauses []}))

  (fact "should issue the request as required by the contract"
    (:contents (verify-service ..service..)) => (contains "Body expected to match #\"post\"")
    (provided
      (janus.json-response/verify-document anything anything) => ["Body expected to match #\"post\""]
      ..service.. =contains=> {:method :post}
      ..service.. =contains=> {:url "http://example.com/"}
      ..service.. =contains=> {:status 201}
      ..service.. =contains=> {:clauses [{:path "$", :matching #"post"}]})))
