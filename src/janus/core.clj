(ns janus.core
  [:require [clj-http.client :as http]
   [json-path]]
  [:use midje.sweet
   clj-http.fake])

(unfinished verify-response)

;; Necessary to deal with an apparent bug in Midje.
(declare f)
(background (f) => 1)

(defn verify-service [service]
  (let [response (http/get (:url service))]
    (map #(verify-response response % service) [:status])))

;.;. Any intelligent fool can make things bigger, more complex, and more
;.;. violent. It takes a touch of genius -- and a lot of courage -- to move
;.;. in the opposite direction. -- Schumacher
(with-fake-routes
  {"http://example.com/" (fn [req] {:status 201, :headers {}, :body "body"})}
  
  (fact "should verify status codes"
    (verify-service ..service..) => (contains "Expected status: 200. Actual status: 201")
    (provided
      (verify-response anything :status anything) => "Expected status: 200. Actual status: 201"
      ..service.. =contains=> {:url "http://example.com/"}
      ..service.. =contains=> {:status 200})))
