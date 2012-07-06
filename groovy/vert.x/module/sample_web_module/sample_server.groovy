
import org.vertx.groovy.core.http.RouteMatcher

Object[] run() {
	println "** run ${vertx}, ${container}"

	def rm = new RouteMatcher()

	rm.get '/sample', { req ->
		req.response.end 'test data'
	}

	rm.get '/sample/:id', { req ->
		req.response.end "data : ${req.params['id']}"
	}

	server = vertx.createHttpServer()
	server.requestHandler(rm.asClosure()).listen 8080

	vertx.fileSystem.writeFileSync 'server.log', "${new Date()} : started"
}

def vertxStop() {
	server.close()
	println "stop"
}

