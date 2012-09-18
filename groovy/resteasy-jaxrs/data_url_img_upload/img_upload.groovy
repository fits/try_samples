@GrabResolver(name = 'jboss', root = 'http://repository.jboss.org/nexus/content/groups/public/')
@Grapes([
	@Grab('org.jboss.resteasy:resteasy-jaxrs:2.3.4.Final'),
	@Grab('org.jboss.resteasy:tjws:2.3.4.Final'),
	@GrabExclude('commons-codec:commons-codec:1.4')
])
import javax.ws.rs.*

import org.jboss.resteasy.util.Base64
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer

@Path("/")
public class ImageUploadResource {
	@GET
	public String getIndexPage() {
		new File("index.html").getText("UTF-8")
	}

	@POST
	@Path("upload")
	public boolean uploadImage(@FormParam("name") String name, @FormParam("img") String img) {
		def result = false
		def m = img =~ /data:([^;,]*)(;base64)?,(.+)/

		if (m) {
			new File(name).bytes = Base64.decode(m.group(3))
			result = true
		}
		result
	}
}

def port = 8081
def server = new TJWSEmbeddedJaxrsServer()

server.setPort(port)
server.deployment.actualResourceClasses.add(ImageUploadResource.class)

server.start()
