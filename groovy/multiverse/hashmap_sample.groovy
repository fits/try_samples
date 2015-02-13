@Grab('org.multiverse:multiverse-core:0.7.0')
import org.multiverse.api.StmUtils
import org.multiverse.api.callables.TxnVoidCallable

def map = StmUtils.newTxnHashMap()

try {
	StmUtils.atomic {
		println map

		map.put('a', 100)
		map.put('b', 200)

		println map.get('a')

		// rollback
		throw new RuntimeException()
	}
} catch (e) {
	println "ERROR: ${e}"
}

StmUtils.atomic {
	// null
	println map.get('a')

	map.put('c', 300)
}

StmUtils.atomic {
	// 300
	println map.get('c')
}
