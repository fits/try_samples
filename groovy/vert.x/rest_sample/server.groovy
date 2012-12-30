import org.vertx.groovy.core.http.RouteMatcher
import org.vertx.java.core.json.impl.Json

def rm = new RouteMatcher()

rm.get '/user/:id', { req ->
	def res = req.response

	res.putHeader('Content-Type', 'application/json')
	res.end Json.encode([
		id: req.params['id'],
		name: 'test'
	])
}

vertx.createHttpServer().requestHandler(rm.asClosure()).listen 8080

println "server started ..."
