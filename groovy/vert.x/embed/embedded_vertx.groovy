@Grab('io.vertx:vertx-core:2.0.0-final')
@Grab('io.vertx:lang-groovy:2.0.0-final')
import org.vertx.groovy.core.Vertx

def vertx = Vertx.newVertx()

vertx.createHttpServer().requestHandler {req ->
	req.response.end "test data"

}.listen 8080

println "started ..."

System.in.read()
