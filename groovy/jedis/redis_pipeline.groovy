@Grab('redis.clients:jedis:2.7.2')
import redis.clients.jedis.Jedis

def jedis = new Jedis()

def p = jedis.pipelined()

p.set(args[0], args[1])
p.expire(args[0], args[2] as int)
def fg = p.get(args[0])

p.sync()
println fg.get()

jedis.close()
