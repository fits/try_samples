@Grab('redis.clients:jedis:2.7.2')
import redis.clients.jedis.Jedis

def jedis = new Jedis()

jedis.keys('*').each { 
	println "key: ${it}, value: ${jedis.get(it)}, type: ${jedis.type(it)}"
}

jedis.close()
