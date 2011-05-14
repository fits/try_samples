package fits.sample

import scala.collection.JavaConversions._
import com.google.code.morphia.Morphia
import com.mongodb.Mongo

object Sample {

	def main(args: Array[String]) {
		val mongo = new Mongo("localhost")
		val db = new Morphia().createDatastore(mongo, "book_review")

		db.save[User](new User("tester1"))

		db.find(classOf[User]).asList.foreach {u =>
			println("user: id = " + u.id + ", name = " + u.name)
		}
	}
}