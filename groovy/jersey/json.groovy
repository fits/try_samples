
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

import javax.xml.bind.annotation.*

import com.sun.net.httpserver.HttpHandler
import com.sun.jersey.api.container.httpserver.HttpServerFactory
import com.sun.jersey.api.container.ContainerFactory

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Customer {
	@XmlElement
	String id
	@XmlElement
	String name
}

@Path("list")
public class SimpleResource {

    @GET
    @Path("{id}")
    @Produces(["application/json"])
    public Customer getCustomer(@PathParam("id") String id) {
        return new Customer(id: id, name: "テストデータ")
    }

    @GET
    @Produces(["application/json"])
    public List<Customer> getCustomerList() {
        return [
        	new Customer(id: "id1", name: "テストデータ"),
        	new Customer(id: "id2", name: "テストデータ2"),
        	new Customer(id: "id3", name: "テストデータ3")
        ]
    }
}

def server = HttpServerFactory.create("http://localhost:8082/", ContainerFactory.createContainer(HttpHandler.class, [SimpleResource.class] as Set))

server.start()

println "server start ..."
println "please enter key to stop"

System.in.read()

server.stop(0)
