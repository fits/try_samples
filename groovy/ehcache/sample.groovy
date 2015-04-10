
@Grab('org.ehcache:ehcache:3.0.0.m1')
import static org.ehcache.config.CacheConfigurationBuilder.newCacheConfigurationBuilder
import static org.ehcache.CacheManagerBuilder.newCacheManagerBuilder

import groovy.transform.ToString

@ToString
class Data {
	String name
	int value
}

def manager = newCacheManagerBuilder()
	.withCache('sample', newCacheConfigurationBuilder().buildConfig(String, Data))
	.build()

def cache = manager.getCache('sample', String, Data)

def d = new Data(name: 'data1', value: 10)

cache.put('d1', d)

println d
// Data(data1, 10)
println cache.get('d1')

// true
println d == cache.get('d1')

d.value = 20
// Data(data1, 20)
println cache.get('d1')

manager.close()
