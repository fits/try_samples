
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.Path
import javax.ws.rs.PathParam

import com.sun.jersey.api.core.ClassNamesResourceConfig
import com.sun.jersey.api.container.httpserver.HttpServerFactory
import com.sun.jersey.api.container.ContainerFactory

@Path("message")
class SimpleResource {

	@GET
	@Produces(Array("text/plain"))
	def getMessage(): String = {
		"てすとめっせーじ"
	}

	@GET
	@Path("{id}")
	@Produces(Array("text/plain"))
	def find(@PathParam("id") id: String): String = {
		"test" + id;
	}
}

object JerseySimpleTest extends Application {
	val server = HttpServerFactory.create("http://localhost:8082/", new ClassNamesResourceConfig(classOf[SimpleResource]))


	server.start()

	println("server start ...")
	println("please enter key to stop")

	System.in.read()

	server.stop(0)
}

