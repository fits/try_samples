@Grab('io.vertx:vertx-core:2.1M1')
@Grab('io.vertx:lang-groovy:2.0.0-final')
import org.vertx.groovy.core.Vertx
import java.util.concurrent.CountDownLatch

def CONTENTS_DIR = args[0]

def vertx = Vertx.newVertx()

vertx.createHttpServer().requestHandler({ req ->
	def path = req.path

	if (path.split('/|\\\\').contains('..')) {
		req.response.setStatusCode(404).end()
	}
	else {
		if (path == '/') {
			path += 'index.html'
		}
		else if (!path.startsWith('/')) {
			path = "/${path}"
		}
	}

	req.response.sendFile("${CONTENTS_DIR}${path}")

}).listen 8080

println "started ..."

def stopLatch = new CountDownLatch(1)

Runtime.runtime.addShutdownHook { ->
    println 'shutdown ...'
    stopLatch.countDown()
}

stopLatch.await()
