@GrabResolver(name = 'jboss', root = 'http://repository.jboss.org/nexus/content/groups/public/')
@Grapes([
	@Grab('org.jboss.resteasy:resteasy-jaxrs:2.3.4.Final'),
	@Grab('org.jboss.resteasy:resteasy-jackson-provider:2.3.4.Final'),
	@Grab('org.jboss.resteasy:tjws:2.3.4.Final'),
	@GrabExclude('commons-codec:commons-codec:1.4')
])
import javax.ws.rs.*
import javax.xml.bind.annotation.*

import org.jboss.resteasy.util.Base64
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
class Data {
	@XmlElement String id
	@XmlElement String title
	@XmlElement String action
}

@Path("/")
public class BatchSaveResource {

	@GET
	@Path("/{type:a|b}")
	@Produces(["application/json"])
	public List<Data> getData(@PathParam("type") String type) {
		[
			new Data(id: "id:1${type}", title: "test data1 ${type}"),
			new Data(id: "id:2${type}", title: "test data2 ${type}"),
		]
	}

	@GET
	@Path("/{filePath:.*}")
	public byte[] getPage(@PathParam("filePath") String filePath) {
		new File(filePath).bytes
	}

	@PUT
	@Consumes(["application/json"])
	@Path("{id}")
	public String putData(@PathParam("id") String id, String json) {
		println json
		"ok"
	}

	@POST
	@Consumes(["application/json"])
	@Produces(["application/json"])
	@Path("/save")
	public boolean postData(List<Data> list) {
		println list
		true
	}
}

def port = 8081
def server = new TJWSEmbeddedJaxrsServer()

server.setPort(port)
server.deployment.actualResourceClasses.add(BatchSaveResource.class)

server.start()
