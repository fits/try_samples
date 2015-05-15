@Grab('redis.clients:jedis:2.7.2')
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPubSub

import groovyx.gpars.GParsPool

GParsPool.withPool {
	def jedis = new Jedis()

	def listener = new JedisPubSub() {
		@Override
		void onMessage(String channel, String message) {
			println "ch: ${channel}, message: ${message}"
		}
	}

	def subscribe = { jedis.subscribe(listener, '__keyevent@0__:expired') }.async()

	def future = subscribe()

	println 'press any key to stop'

	System.in.read()

	listener.unsubscribe()

	future.get()
}
