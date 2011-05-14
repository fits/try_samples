package fits.sample

import java.util.Date
import scala.collection.JavaConversions._
import com.google.code.morphia.Morphia
import com.mongodb.Mongo

object Sample {

	def main(args: Array[String]) {
		val mongo = new Mongo("localhost")
		val db = new Morphia().createDatastore(mongo, "book_review")

		val u1 = new User("user1")

		db.save[User](u1)

		db.find(classOf[User]).asList.foreach {u =>
			println("user: id = " + u.id + ", name = " + u.name)
		}

		val b1 = new Book("ドメイン駆動設計", "9784798121963")
		b1.comments.add(new Comment("test", new Date(), u1))

		db.save[Book](b1)

		db.find(classOf[Book]).asList.foreach {b =>
			println("book: id = " + b.id + ", title = " + b.title + ", isbn = " + b.isbn)

			b.comments.foreach {c =>
				println("    comment: content = " + c.content + ", date = " + c.createdDate + ", user = " + c.user.name)
			}
		}
	}
}