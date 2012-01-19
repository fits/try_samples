@GrabResolver(name = 'jboss', root = 'http://repository.jboss.org/nexus/content/groups/public/')
@Grab('org.jboss.resteasy:resteasy-jaxrs:2.3.1.GA')
@Grab('org.jboss.resteasy:tjws:2.3.1.GA')
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
server.deployment.actualResourceClasses.add(SimpleResource.class)

server.start()
