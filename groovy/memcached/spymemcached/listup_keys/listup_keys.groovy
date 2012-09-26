/*
 * memcached のキー一覧をリストアップするスクリプト
 */
@GrabResolver(name = "couchbase.com", root = "http://files.couchbase.com/maven2/")
@Grab("spy:spymemcached:2.8.7")
import net.spy.memcached.MemcachedClient

def cacheServer = 'localhost'
def cacheServerPort = 11211

class CacheItem {
	def key
	def size
	Date expire
}

def client = new MemcachedClient(new InetSocketAddress(cacheServer, cacheServerPort))

def getKeys = {cl ->
	cl.getStats('items').collectMany {k, v ->

		def slabClasses = v.keySet().findResults {
			def p = it.split(':')
			(p.length > 1)? p[1]: null
		}.unique()

		slabClasses.collectMany {
			client.getStats("cachedump $it 0").collectMany {dk, dv ->
				dv.findResults {ik, iv ->
					def m = iv =~ /\[(.*) b; (.*) s\]/

					if (m) {
						def size = m.group(1)
						def expire = new Date(Long.parseLong(m.group(2)) * 1000)

						new CacheItem(key: ik, size: size, expire: expire)
					}
					else {
						null
					}
				}
			}
		}
	}
}

println "----- keys -----"
getKeys(client).each {println "key = ${it.key}, size = ${it.size}, expire = ${it.expire}"}
println "----------------"

client.shutdown()
