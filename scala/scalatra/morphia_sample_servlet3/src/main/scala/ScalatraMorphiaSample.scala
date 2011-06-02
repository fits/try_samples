package fits.sample

import scala.collection.JavaConverters._
import javax.servlet.annotation.WebServlet
import org.scalatra.ScalatraServlet
import org.scalatra.scalate.ScalateSupport
import com.google.code.morphia.Morphia
import com.mongodb.Mongo
import org.bson.types.ObjectId

import fits.sample.models._

@WebServlet(Array("/*"))
class ScalatraMorphiaSample extends ScalatraServlet with ScalateSupport {

	val db = new Morphia().createDatastore(new Mongo("localhost"), "book_review")

	beforeAll {
		contentType = "text/html"
	}

	get("/") {
		val books: Iterable[Book] = db.find(classOf[Book]).order("title").asList.asScala
		val users: Iterable[User] = db.find(classOf[User]).order("name").asList.asScala

		templateEngine.layout("index.scaml", Map(
			"books" -> books,
			"users" -> users,
			"action" -> "/comments"
		))
	}

	get("/books") {
		val books: Iterable[Book] = db.find(classOf[Book]).order("title").asList.asScala

		templateEngine.layout("book.scaml", Map(
			"books" -> books,
			"action" -> "/books"
		))
	}

	post("/books") {
		db.save[Book](new Book(params("title"), params("isbn")))

		redirect("/books")
	}

	post("/comments") {
		val book = db.get(classOf[Book], new ObjectId(params("book")))
		val user = db.get(classOf[User], new ObjectId(params("user")))

		book.comments.add(new Comment(params("content"), user))
		db.save[Book](book)

		redirect("/")
	}

	get("/users") {
		val users: Iterable[User] = db.find(classOf[User]).order("name").asList.asScala

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

