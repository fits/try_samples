package fits.sample

import org.vertx.groovy.core.Vertx
import java.util.concurrent.CountDownLatch

def config = new Properties()
config.load(getClass().getClassLoader().getResourceAsStream('app.properties'))

def vertx = Vertx.newVertx()

vertx.createHttpServer().requestHandler(RoutingFactory.create()).listen(config.port as int)

println "started [${config.port}] ..."

def stopLatch = new CountDownLatch(1)

Runtime.runtime.addShutdownHook { ->
	println 'shutdown ...'
	stopLatch.countDown()
}

stopLatch.await()