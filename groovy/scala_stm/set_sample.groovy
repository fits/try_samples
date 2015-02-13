@Grab('org.scala-stm:scala-stm_2.11:0.7')
import scala.concurrent.stm.japi.STM

def set = STM.newSet()

println set

set.add('b')
set.add('a')

println set

try {
	STM.atomic {
		set.add('c')

		// rollback
		throw new RuntimeException()
	}
} catch (e) {
	println "ERROR: ${e}"
}

// [b, a]
println set

STM.atomic {
	set.add('1')
	set.add('d')
}

// [1, d, b, a]
println set
