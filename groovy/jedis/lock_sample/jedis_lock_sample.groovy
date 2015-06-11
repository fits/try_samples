@Grab('redis.clients:jedis:2.7.2')
import redis.clients.jedis.Jedis

def retryCounter = 10
def sleepTime = 5000
def lockTime = 60000

def jedis = new Jedis()

def key = args[0]
def value = args[1]

def lockId = "lock-${key}" as String
def lockValue = UUID.randomUUID().toString()

while (retryCounter-- > 0) {
	// lock (expire 60 seconds)
	def lock = jedis.set(lockId, lockValue, 'NX', 'PX', lockTime)

	if (lock != null) {
		println jedis.watch(key)

		def curValue = jedis.get(key)

		sleep 5000

		// transaction
		def tr = jedis.multi()

		tr.set(key, "${value} - ${curValue}")

		// execute
		if (tr.exec() == null) {
			println '*** Failed transaction'
		}

		// unlock
		if (jedis.get(lockId) == lockValue) {
			jedis.del(lockId)
		}

		println jedis.get(key)
		break
	}

	println 'wait and retry'

	sleep sleepTime
}

jedis.close()
