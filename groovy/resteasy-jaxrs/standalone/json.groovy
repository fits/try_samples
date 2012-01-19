@GrabResolver(name = 'jboss', root = 'http://repository.jboss.org/nexus/content/groups/public/')
@Grab('org.jboss.resteasy:resteasy-jaxrs:2.3.1.GA')
@Grab('org.jboss.resteasy:resteasy-jettison-provider:2.3.1.GA')
@Grab('org.jboss.resteasy:tjws:2.3.1.GA')
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.xml.bind.annotation.*

import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
class Customer {
	@XmlElement
	String id
	@XmlElement
	String name
}

@Path("list")
public class SimpleResource {

    @GET
    @Produces(["application/json"])
    public List<Customer> getCustomerList() {
		[
			new Customer(id: "id:1", name: "テストデータ"),
			new Customer(id: "id:2", name: "チェック")
		]
    }

    @GET
    @Path("{id}")
    @Produces(["application/json"])
    public Customer getCustomer(@PathParam("id") String id) {
		new Customer(id: id, name: "test")
    }

}

def server = new TJWSEmbeddedJaxrsServer()

server.setPort(8081)
server.deployment.actualResourceClasses.add(SimpleResource.class)

server.start()
