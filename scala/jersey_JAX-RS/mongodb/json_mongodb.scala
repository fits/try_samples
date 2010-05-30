
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.Consumes

import com.sun.jersey.api.core.ClassNamesResourceConfig
import com.sun.jersey.api.container.httpserver.HttpServerFactory
import com.sun.jersey.api.container.ContainerFactory

import com.mongodb._
import com.mongodb.util.JSON

@Path("customers")
class CustomerResource {

	val db = new Mongo("localhost").getDB("test")
	val col = db.getCollection("customer")

	@POST
	@Consumes(Array("application/json"))
	def addCustomer(data: String) = {
		col.insert(data)
	}

    @GET
    @Produces(Array("application/json"))
    def getCustomerList(): String = {
		toJson(col.find())
    }

	@GET
    @Path("{title}")
    @Produces(Array("application/json"))
    def searchCustomerList(@PathParam("title") title: String): String = {
		toJson(col.find(new BasicDBObject("title", title)))
	}

	implicit def toDBObject(json: String): Array[DBObject] = {
		JSON.parse(json) match {
			case o: DBObject => Array(o)
			case l: List[DBObject] => l.toArray
			case _ => Array()
		}
	}

	implicit def toJson(cursor: DBCursor): String = {
		JSON.serialize(cursor.toArray())
	}
}

object JerseyJsonTest extends Application {

	val server = HttpServerFactory.create("http://localhost:8082/", new ClassNamesResourceConfig(classOf[CustomerResource]))

	server.start()

	println("server start ...")
	println("please press Ctrl + C to stop")
}