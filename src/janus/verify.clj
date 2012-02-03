(ns janus.verify
  [:require [janus.json-response]
   [json-path]
   [clj-http.client :as http]])

(defn extract-clause [clause contract context]
  (filter #(= clause (nth % 0))
          (concat (:clauses contract) (:clauses context))))

(defmulti check-clause (fn [response clause] (nth clause 0)))

(defmethod check-clause :status [response clause]
  (let [actual (:status response)
        expected (nth clause 1)]
    (if (not= expected actual)
      (str "Expected status " expected ". Got status " actual))))

(defmethod check-clause :header [response clause]
  (let [[_ header-name comparison expected] clause
        actual (-> response :headers (get header-name))]
    (cond
     (= :equal-to comparison) (if (not= expected actual)
                               (str "Expected header '" header-name "' to equal '" expected "'. Got '" actual "'.")))))

(defn errors-in-envelope [response contract context]
  (concat
   (map (partial check-clause response)
        (extract-clause :status contract context))
   (map (partial check-clause response)
        (extract-clause :header contract context))))

(defn errors-in-body [response contract context]
  (let [doc-type (-> response :headers (get "Content-Type"))
        body (:body response)
        clauses (concat (:clauses contract) (:clauses context))]
    (cond
     (or (= "application/json" doc-type)
         (re-seq #"\+json" doc-type)) (janus.json-response/verify-document body clauses))))

(defn property [prop-name contract context]
  (:value (first (filter #(= prop-name (:name %))
                         (concat (:properties contract) (:properties context))))))

(defn verify-contract [contract context]
  (let [response (http/request {:method (property "method" contract context),
                                :url (property "url" contract context)})
        envelope-errors (errors-in-envelope response contract context)
        body-errors (errors-in-body response contract context)
        errors (concat envelope-errors body-errors)]
    (if (empty? errors)
      [(:name contract) :succeeded]
      [(:name contract) :failed errors])))

(defn verify-service [service context]
  (filter #(not= nil %)
          (map #(verify-contract % context) (:contracts service))))
