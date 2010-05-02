
def str = "abc-def|12d2f3"

(str =~ /d.f/).each {
	println "match : ${it}"
}

