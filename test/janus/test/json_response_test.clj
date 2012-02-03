(ns janus.test.json-response-test
  [:use janus.json-response
   midje.sweet])

(unfinished )

(fact
  (extract-rule {:path "" :equal-to "b"}) => :equal-to)

(fact
  (equal-to :hello :hello) => nil
  (equal-to :world :hello) => "Expected \":world\". Got \":hello\"")

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

(fact
  (matching #"[a-z]+" "hello") => nil
  (matching #"[a-z]+" "100") => "Expected \"100\" to match regex [a-z]+")

(fact
  (verify-document "\"body\"" [{:path "$", :equal-to "body"}]) => empty?)
