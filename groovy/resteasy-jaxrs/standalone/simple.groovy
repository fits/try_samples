
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer


@Path("list")
public class SimpleResource {

    @GET
    @Path("{id}")
    @Produces(["text/plain"])
    public String getMessage(@PathParam("id") String id) {
        return "テストデータ:${id}"
    }
}

def server = new TJWSEmbeddedJaxrsServer()

server.setPort(8081)
server.registry.addPerRequestResource(SimpleResource.class)

server.start()
