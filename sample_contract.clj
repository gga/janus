(environment :dev (fn []))

(service
 (method :post)
 (header "Content-Type" "application/json")

 (before (fn [config]
           ;; Perform a POST to authenticate, for example

           (add-cookie config "SESSION" (-> :cookie-jar response (get "SESSION_ID")))
           ))

 (after (fn []))

 (contract
  "payment recipient list"
  (definition
    (url "/recipients"))

  (clauses
   (should_have :path "$..recipients" :of_type :array)
   (should_have :path "$..recipients[*].name", :equal_to "")

   (should_have :path "$.name", :matching #"\w+\w+")))

 (conversation
  "Creating a new send money transaction"

  (contract
   "create transaction"
   :named submit_tx
   (definition ...)
   (clauses ...))

  (contract
   "verify transaction details"
   (definition
     (url "...")
     (body {
            :transactionid (:transaction_id submit_tx)}))

   (clauses
    (should_have :path "", :of_type :string)))))