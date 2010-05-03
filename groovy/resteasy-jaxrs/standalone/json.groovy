
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

import net.sf.json.groovy.JsonGroovyBuilder
import net.sf.json.JSONSerializer

import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer

class Customer {
	def id
	def name
}

@Path("list")
public class SimpleResource {

    @GET
    @Produces(["application/json"])
    public String getCustomerList() {
		def builder = new JsonGroovyBuilder()

		return builder.customers {
			customer {
				id = "id:1"
				name = "テストデータ"
			}
			customer {
				id = "id:2"
				name = "チェック"
			}
		}
    }

    @GET
    @Path("{id}")
    @Produces(["application/json"])
    public String getCustomer(@PathParam("id") String id) {
		return JSONSerializer.toJSON(new Customer(id: id, name: "test"))
    }

}

def server = new TJWSEmbeddedJaxrsServer()

server.setPort(8081)
server.registry.addPerRequestResource(SimpleResource.class)

server.start()
