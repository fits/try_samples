@Grab("org.functionaljava:functionaljava:3.1")
import fj.F
import fj.F2
import static fj.data.List.*

def data = list(1, 3, 5, 7)

def r1 = data.foldLeft1({b, a ->
	println "$b, $a"
	b + a
} as F2)

println "r1 = $r1"


def r2 = data.foldLeft1({b ->
	{ a -> 
		println "$b, $a"
		b + a
	} as F
} as F)

println "r2 = $r2"


def r3 = data.foldLeft({b, a ->
	println "$b, $a"
	b + a
} as F2, 0)

println "r3 = $r3"
