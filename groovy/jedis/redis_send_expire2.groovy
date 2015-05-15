@Grab('redis.clients:jedis:2.7.2')
import redis.clients.jedis.Jedis

def jedis = new Jedis()

def res = jedis.set(args[0], args[1], 'NX', 'EX', args[2] as long)

println res

jedis.close()
