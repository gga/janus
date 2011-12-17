(ns janus.regex-unify
  [:require [clojure.core.logic :as logic]])

(defn make-char-set [start end]
  (vec (map char (range (int start) (inc (int end))))))

(defn charo [q chr matched]
  (logic/conde [(logic/== q chr)
                (logic/== matched true)]
               [(logic/!= q chr)
                (logic/== matched false)]))

(defn containso [q sequence found]
  (logic/conde [(logic/== true (empty? sequence))
                (logic/== false found)]
               [(logic/!= nil (first sequence))
                (logic/== q (first sequence))
                (logic/== true found)] 
               [(if (empty? sequence)
                  logic/fail
                  (containso q (rest sequence) found))]))

(defn matching-stringo [q chr-set length]
  (logic/== q (apply str (take length (repeatedly #(nth chr-set (rand-int (count chr-set))))))))

(defn repeato [min max char-set q matched]
  (letfn [(next [length]
            (logic/conde [(logic/== true matched)
                          (matching-stringo q char-set length)]
                         [(logic/== false matched)
                          (logic/== (apply str (repeat length "b")) q)]
                         [(logic/!= length max)
                          (next (inc length))]))]
    (next min)))
