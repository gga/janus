(ns janus.test.json-response-test
  [:use janus.json-response
   midje.sweet])

(unfinished )

(fact
  (extract-rule [:path "" :equal-to "b"]) => :equal-to)

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
  (matching #"[a-z]+" "HELLO") => "Expected \"HELLO\" to match regex [a-z]+"
  (matching #"[a-z]" 10) => "Expected \"10\" to match regex [a-z]")

(fact
  (verify-clause "a" [:path "$.foo[*]" :matching #"[a-z]"]) => nil
  (verify-clause "1" [:path "$.foo[*]" :matching #"[a-z]"]) => "failed, at path $.foo[*]"
  (provided
    (matching #"[a-z]" "1") => "failed"))

(fact
  (verify-seq ["a"] [:path "$.foo[*]" :matching #"[a-z]"]) => []
  (verify-seq ["1"] [:path "$.foo[*]" :matching #"[a-z]"]) => ["failed, at path $.foo[*]"]
  (provided
    (matching #"[a-z]" "1") => "failed"))

(fact
  (verify-document "\"body\"" [[:path "$", :equal-to "body"]]) => empty?
  (verify-document "{\"foo\": [\"a\"]}" [[:path "$.foo[*]", :matching #"[a-z]"]]) => empty?
  (verify-document "{\"foo\": [\"1\"]}" [[:path "$.foo[*]", :matching #"[a-z]"]]) =not=> empty?)
