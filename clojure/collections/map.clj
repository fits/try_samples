(def m {:a "test", :b 100})
(println "map length: " (count m))
(println "array[:a] = " (m :a))
(println "array[:b] = " (m :b))
(println m)

(def nm (into m {:c (* 3 (m :b))}))
(println nm)
