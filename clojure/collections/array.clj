(def l ["a" "test" 1 100])
(println "array length: " (count l))
(println "array[0] = " (l 0))
(println "array[1] = " (l 1))
(println "array[3] = " (l 3))
(println l)

(def nl (into l ["b" "test"]))
(println nl)
