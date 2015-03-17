@Grab('org.glassfish.jersey.containers:jersey-container-grizzly2-http:2.17')
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path('sample')
class SimpleResource {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	String get() {
		'ok'
	}
}

def server = GrizzlyHttpServerFactory.createHttpServer(new URI('http://localhost:8080/'), new ResourceConfig(SimpleResource))

server.start()

println 'started ...'

System.in.read()

server.shutdownNow()
