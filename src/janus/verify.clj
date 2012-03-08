(ns janus.verify
  [:require [janus.json-response]
   [json-path]
   [clojure.data.json :as json]
   [clj-http.client :as http]]
  [:use midje.sweet])

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
  (let [doc-type (-> response :headers (get "content-type"))
        body (:body response)
        clauses (concat (:clauses contract) (:clauses context))]
    (cond
     (or (re-seq #"^application/json" doc-type)
         (re-seq #"\+json" doc-type)) (janus.json-response/verify-document body clauses)
     :else [(str "Unable to verify documents of type '" doc-type "'")])))

(defn property [prop-name contract context]
  (:value (first (filter #(= prop-name (:name %))
                         (concat (:properties contract) (:properties context))))))

(defn headers-from [contract context]
  (reduce (fn [acc v] (conj acc {(:name v) (:value v)}))
          {}
          (concat (:headers contract) (:headers context))))

(defn body-from [contract context]
  (let [body-def (if (contains? contract :body)
                   (:body contract)
                   (:body context))]
    (cond
     (= (:type body-def) :string) (str (:data body-def))
     (= (:type body-def) :json) (json/json-str (:data body-def)))))

(defn verify-contract [contract context]
  (let [response (http/request {:method (property "method" contract context),
                                :url (property "url" contract context)
                                :headers (headers-from contract context)
                                :body (body-from contract context)})
        envelope-errors (errors-in-envelope response contract context)
        body-errors (errors-in-body response contract context)
        errors (concat envelope-errors body-errors)]
    (if (empty? errors)
      [(:name contract) :succeeded]
      [(:name contract) :failed errors])))

(defn verify-service [service context]
  (let [errors (filter #(= :failed (nth % 1))
                       (map #(verify-contract % context) (:contracts service)))]
    (if (empty? errors)
      [(:name service) :succeeded]
      [(:name service) :failed errors])))

;; (fact
;;   (verify-service {:name "simple JSON service"
;;                    :contracts [{:name "GET document"
;;                                 :properties [{:name "method" :value :get}
;;                                              {:name "url" :value "http://localhost:4568/service"}]
;;                                 :headers [{:name "Content-Type" :value "application/json"}]
;;                                 :clauses [[:path "$..id" :of-type :number]
;;                                           [:path "$..features[*]" :matching #"[a-z]"]]}]} {}) => empty?)
