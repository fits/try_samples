
import scala.reflect.BeanProperty
import scala.collection.mutable.ListBuffer

import javax.ws.rs.{GET, POST, PUT, Path, PathParam, Consumes, Produces}
import javax.ws.rs.core.{Response, UriBuilder}

import javax.xml.bind.annotation.XmlRootElement

import com.sun.jersey.api.core.ClassNamesResourceConfig
import com.sun.jersey.api.container.httpserver.HttpServerFactory
import com.sun.jersey.api.container.ContainerFactory
import com.sun.jersey.spi.resource.Singleton

@XmlRootElement
class Task(@BeanProperty var guid: String, @BeanProperty var title: String) {
	//補助コンストラクタ
	//JAXB にはデフォルトコンストラクタが必須
	def this() = this("", "")
}

@Path("tasks")
@Singleton
class TaskResource {
	val taskList = new ListBuffer[Task]

	@POST
	@Consumes(Array("application/json"))
	def createTask(task: Task): Response = {
		println("create task: " + task)

		task.guid = taskList.length.toString
		taskList += task

		val uri = UriBuilder.fromPath("{index}").build(task.guid)

		println(uri)

		return Response.created(uri).build()
	}

	@PUT
    @Path("{index}")
    @Produces(Array("application/json"))
    def updateTask(@PathParam("index") index: Int, task: Task): Task = {
		println("update task: " + index + ", " + task.title)

		val t = taskList(index)

    	t.title = task.title
    	t
    }

    @GET
    @Path("{index}")
    @Produces(Array("application/json"))
    def getTask(@PathParam("index") index: Int): Task = {
    	taskList(index)
    }

    @GET
    @Produces(Array("application/json"))
    def getTaskList(): Array[Task] = {
		println("list")
		taskList.toArray
    }
}

object TaskTest extends Application {

	val server = HttpServerFactory.create("http://localhost:8082/", new ClassNamesResourceConfig(classOf[TaskResource]))

	server.start()

	println("server start ...")
	println("please press Ctrl + C to stop")
}