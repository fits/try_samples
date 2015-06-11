@Grab('redis.clients:jedis:2.7.2')
import redis.clients.jedis.Jedis

def jedis = new Jedis()

println jedis.watch(args[0])

sleep 5000

def value = jedis.get(args[0])

println "value : ${value}"

def t = jedis.multi()

t.set(args[0], "${value}, ${args[1]}")

println t.exec()

println jedis.get(args[0])

jedis.close()