@Grab('redis.clients:jedis:2.1.0')
import redis.clients.jedis.*

def client = new Jedis('localhost')

client.set("sample1", "value1")

println client.get("sample1")
