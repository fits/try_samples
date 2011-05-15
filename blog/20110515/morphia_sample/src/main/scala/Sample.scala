package fits.sample

import scala.collection.JavaConversions._
import com.google.code.morphia.Morphia
import com.mongodb.Mongo

object Sample {

	def main(args: Array[String]) {
		val mongo = new Mongo("localhost")
		val db = new Morphia().createDatastore(mongo, "book_review")

		val u1 = new User("user1")
		db.save[User](u1)

		val u2 = new User("tester1")
		db.save[User](u2)

		val b1 = new Book("ドメイン駆動設計", "9784798121963")
		b1.comments.add(new Comment("test", u1))
		b1.comments.add(new Comment("test2", u2))

		db.save[Book](b1)

		//全件取得
		db.find(classOf[Book]).asList.foreach {b =>
			printf("book: id = %s, title = %s, isbn = %s\n", b.id, b.title, b.isbn)

			b.comments.foreach {c =>
				printf("  comment: content = %s, date = %s, user = %s\n", c.content, c.createdDate, c.user.name)
			}
		}
	}
}