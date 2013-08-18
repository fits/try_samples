@Grab('io.vertx:vertx-core:2.0.1-final')
@Grab('io.vertx:lang-groovy:2.0.0-final')
import org.vertx.groovy.core.Vertx
import java.util.concurrent.CountDownLatch

def vertx = Vertx.newVertx()

vertx.createHttpServer().requestHandler {req ->
	req.response.end 'test data'

}.listen 8080

println 'started ...'

def stopLatch = new CountDownLatch(1)

Runtime.runtime.addShutdownHook { ->
	println 'shutdown ...'
	stopLatch.countDown()
}

stopLatch.await()
