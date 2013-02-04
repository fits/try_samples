package fits.sample

import com.mongodb.casbah._
import com.mongodb.casbah.commons._

object Sample extends App {
	val col = MongoClient()("sampledb")("users")

	col.insert(MongoDBObject(
		"id" -> 1,
		"name" -> "テストユーザー",
		"address" -> MongoDBObject(
			"zip" -> "1111111",
			"city" -> "サンプル"
		)
	))

	col.find.foreach(println)
}
