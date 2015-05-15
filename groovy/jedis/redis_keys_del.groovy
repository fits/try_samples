@Grab('redis.clients:jedis:2.7.2')
import redis.clients.jedis.Jedis

def jedis = new Jedis()

jedis.keys('*').each {
	jedis.del it
}

jedis.close()
