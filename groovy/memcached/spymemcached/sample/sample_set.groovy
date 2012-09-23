
@GrabResolver(name = "couchbase.com", root = "http://files.couchbase.com/maven2/")
@Grab("spy:spymemcached:2.7.3")
import net.spy.memcached.MemcachedClient

def key = args[0]
def value = args[1]

def client = new MemcachedClient(new InetSocketAddress(11211))

def f = client.set(key, 0, value)

println "result : ${f.get()} ($key => $value)"

client.shutdown()
