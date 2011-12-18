
@GrabResolver(name = "couchbase.com", root = "http://files.couchbase.com/maven2/")
@Grab("spy:spymemcached:2.7.3")
import net.spy.memcached.MemcachedClient

def client = new MemcachedClient(new InetSocketAddress(11211))

client.set("a1", 0, "test data")

println client.get("a1")

client.shutdown()
