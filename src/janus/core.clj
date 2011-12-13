(ns janus.core
  [:require [clj-http.client :as client]]
  [:use midje.sweet
        clj-http.fake])

(defn verify-service [service])

(fact "should verify status codes"
  (verify-service {}) => nil?)
