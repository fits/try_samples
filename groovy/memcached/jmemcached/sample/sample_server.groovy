
@Grab("com.thimbleware.jmemcached:jmemcached-core:1.0.0")
@Grab("org.slf4j:slf4j-jdk14:1.6.3")
import com.thimbleware.jmemcached.*
import com.thimbleware.jmemcached.storage.hash.*

def server = new MemCacheDaemon()
server.addr = new InetSocketAddress(11211)

server.cache = new CacheImpl(ConcurrentLinkedHashMap.create(ConcurrentLinkedHashMap.EvictionPolicy.FIFO, 100, 1000000))

//server.binary = true
server.verbose = true

server.start()

println "press key to stop"
System.in.read()

server.stop()

