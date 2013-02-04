package fits.sample

import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers.DefaultBSONHandlers._
import reactivemongo.core.commands._

import scala.concurrent.duration._
import scala.util.{Success, Failure}
import play.api.libs.iteratee.Iteratee

object Sample extends App {
	import scala.concurrent.ExecutionContext.Implicits.global

	val mongo = MongoConnection(List("localhost:27017"))
	val db = mongo("sampledb")
	val col = db("users")

	val doc = BSONDocument(
		"id" -> BSONInteger(2),
		"name" -> BSONString("テストユーザー3"),
		"address" -> BSONDocument(
			"zip" -> BSONString("22211111"),
			"city" -> BSONString("test3")
		)
	)

	val future = col.insert(doc, GetLastError())

	future.onComplete {
		case Success(v) => {
			println(s"success : ${v}")

			val cursor = col.find(BSONDocument())

			cursor.enumerate.apply(Iteratee.foreach[TraversableBSONDocument] { d =>
				println(BSONDocument.pretty(d))
			})

			mongo.askClose()(Duration(5, SECONDS))
		}
		case Failure(e) => e.printStackTrace()
	}
}
