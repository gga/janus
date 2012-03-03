(ns janus.text-presentation)

(defn failed-contract [[name _ msgs]]
  (str "\t" name ":\n"
       (apply str (map #(str "\t\t" % "\n") msgs))))

(defn failed-service [[name _ contracts]]
  (str name ":\n"
       (apply str (map failed-contract
                       (filter #(= :failed (nth % 1)) contracts)))))

(defn display [services]
  (let [service-count (count services)
        failed-svcs (filter #(= :failed (nth % 1)) services)
        failed (count failed-svcs)]
    (str service-count
         " service"
         (if (not= 1 service-count)
           "s"
           "")
         " ("
         failed
         " failed)\n"
         (apply str (map failed-service failed-svcs)))))
