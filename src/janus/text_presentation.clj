(ns janus.text-presentation
  [:use midje.sweet])

(unfinished)

(defn display [services]
  (let [succeeded (count (filter #(= :succeeded (nth % 1)) services))
        failed (- (count services) succeeded)]
    (str succeeded
         " service"
         (if (not= 1 succeeded)
           "s"
           "")
         " ("
         failed
         " failed)")))

;.;. Achievement is its own reward. -- David Lynch
;; (fact
;;   (display [["sample" :succeeded]]) => "1 service (0 failed)")
