
@GrabResolver(name = "couchbase.com", root = "http://files.couchbase.com/maven2/")
@Grab("spy:spymemcached:2.7.3")
import net.spy.memcached.MemcachedClient

def key = args[0]

def client = new MemcachedClient(new InetSocketAddress(11211))

def res = client.get(key)

println "result : $res"

client.shutdown()
