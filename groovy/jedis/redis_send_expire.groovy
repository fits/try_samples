@Grab('redis.clients:jedis:2.7.2')
import redis.clients.jedis.Jedis

def jedis = new Jedis()

def res = jedis.setex(args[0], args[2] as int, args[1])

println res

jedis.close()
