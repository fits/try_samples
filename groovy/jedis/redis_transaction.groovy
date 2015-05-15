@Grab('redis.clients:jedis:2.7.2')
import redis.clients.jedis.Jedis

def jedis = new Jedis()

def t = jedis.multi()

t.set(args[0], args[1])
t.expire(args[0], args[2] as int)
def fg = t.get(args[0])

println t.exec()
println fg.get()

jedis.close()
