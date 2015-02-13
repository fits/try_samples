@Grab('org.scala-stm:scala-stm_2.11:0.7')
import scala.concurrent.stm.japi.STM

def map = STM.newMap()

try {
	STM.atomic {
		println map

		map.put('a', 100)
		map.put('b', 200)

		println map

		// rollback
		throw new RuntimeException()
	}
} catch (e) {
	println "ERROR: ${e}"
}

println map

STM.atomic {
	// null
	println map.get('a')

	map.put('c', 300)
}

println map
