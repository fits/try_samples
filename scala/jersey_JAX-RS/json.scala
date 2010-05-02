
import scala.reflect.BeanProperty

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

import javax.xml.bind.annotation.XmlRootElement

import com.sun.jersey.api.core.ClassNamesResourceConfig
import com.sun.jersey.api.container.httpserver.HttpServerFactory
import com.sun.jersey.api.container.ContainerFactory

@XmlRootElement
class Customer(@BeanProperty var id: String, @BeanProperty var name: String) {

	//補助コンストラクタ
	//JAXB にはデフォルトコンストラクタが必須
	def this() = this("", "")
}

@Path("list")
class CustomerResource {

    @GET
    @Path("{id}")
    @Produces(Array("application/json"))
    def getCustomer(@PathParam("id") id: String): Customer = {
    	new Customer(id, "テストデータ")
    }

    @GET
    @Produces(Array("application/json"))
    def getCustomerList(): Array[Customer] = {
		//戻り値の型が scala.List だと例外が発生するので注意
        Array(
        	new Customer("id1", "テストデータ"),
        	new Customer("id2", "テストデータ2"),
        	new Customer("id3", "テストデータ3")
        )
    }
}

object JerseyJsonTest extends Application {

	val server = HttpServerFactory.create("http://localhost:8082/", new ClassNamesResourceConfig(classOf[CustomerResource]))

	server.start()

	println("server start ...")
	println("please press Ctrl + C to stop")
}