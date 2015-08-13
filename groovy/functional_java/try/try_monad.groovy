@Grab('org.functionaljava:functionaljava:4.4')
import fj.Try
import fj.function.Try0

def printValidation = { v ->
	if (v.isSuccess()) {
		println "success : ${v.success()}"
	}
	else {
		println "failed : ${v.fail()}"
	}
}

def t1 = { 'sample' } as Try0<String, RuntimeException>
def t2 = { throw new IllegalStateException() } as Try0<String, RuntimeException>
def r1 = Try.f(t1)

printValidation r1.f()

def r2 = Try.f(t2)

printValidation r2.f()

printValidation r1.bind { v1 -> 
	r1.map { v2 -> 
		v1.bind { a1 -> 
			v2.map { a2 -> 
				"${a1}-${a2}"
			}
		}
	}
}.f()

printValidation r1.bind { v1 -> 
	r2.map { v2 -> 
		v1.bind { a1 -> 
			v2.map { a2 -> 
				"${a1}-${a2}"
			}
		}
	}
}.f()
