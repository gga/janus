(ns janus.test.verify-test
  [:use janus.text-presentation
   midje.sweet])

(fact
  (failed-contract ["contract" :failed ["failure"]]) => "\tcontract:\n\t\tfailure\n")

(fact
  (failed-service ["sample" :failed [["contract" :failed ["message"]]]]) => "sample:\n\tcontract:\n\t\tmessage\n"
  (failed-service ["sample" :failed [["a" :succeeded]
                                     ["b" :failed ["msg"]]]]) => "sample:\n\tb:\n\t\tmsg\n")

(fact
  (display [["sample" :succeeded]]) => "1 service (0 failed)\n"
  (display [["sample" :failed "svc contracts"]]) => "1 service (1 failed)\nsvc-failure"
  (provided
    (failed-service ["sample" :failed "svc contracts"]) => "svc-failure"))
