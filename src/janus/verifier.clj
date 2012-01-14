(ns janus.verifier
  [:require [clj-http.client :as http]
   [json-path]]
  [:use midje.sweet
   clj-http.fake
   [clojure.data.json :only [read-json]]])

(unfinished )

(defn verify-response-body [body clause])

(fact "should verify that the body matches")

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
        body (read-json (:body response))]
    (filter #(not (nil? %)) (concat
                             (map #(verify-response response % service) [:status])
                             (map #(verify-response-body body %) (:clauses service))))))

(with-fake-routes
  {"http://example.com/" (fn [req] {:status 201,
                                    :headers {},
                                    :body (str "\"body via " (name (:request-method req)) "\"")})}
  
  (fact "should verify status codes"
    (verify-service ..service..) => (contains "Expected status: 200. Actual status: 201")
    (provided
      ..service.. =contains=> {:method :get}
      ..service.. =contains=> {:url "http://example.com/"}
      ..service.. =contains=> {:status 200}
      ..service.. =contains=> {:clauses []}))

  (fact "should issue the request as required by the contract"
    (verify-service ..service..) => (contains "Body expected to match #\"post\"")
    (provided
      (verify-response-body "body via post" anything) => "Body expected to match #\"post\""
      ..service.. =contains=> {:method :post}
      ..service.. =contains=> {:url "http://example.com/"}
      ..service.. =contains=> {:status 201}
      ..service.. =contains=> {:clauses [{:path "$", :matching #"post"}]})))
