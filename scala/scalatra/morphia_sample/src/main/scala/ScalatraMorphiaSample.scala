package fits.sample

import scala.collection.JavaConverters._
import org.scalatra.ScalatraServlet
import org.scalatra.scalate.ScalateSupport
import com.google.code.morphia.Morphia
import com.mongodb.Mongo

import fits.sample.models._

class ScalatraMorphiaSample extends ScalatraServlet with ScalateSupport {

	val db = new Morphia().createDatastore(new Mongo("localhost"), "book_review")

	beforeAll {
		contentType = "text/html"
	}

	get("/users") {
		val users: Iterable[User] = db.find(classOf[User]).asList.asScala

		templateEngine.layout("user.scaml", Map(
			"users" -> users,
			"action" -> "/users"
		))
	}

	post("/users") {
		db.save[User](new User(params("name")))

		redirect("/users")
	}
}

