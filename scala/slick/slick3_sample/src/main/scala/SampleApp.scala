package sample

import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

class Products(tag: Tag) extends Table[(Int, String, Double)](tag, "product") {
	def id = column[Int]("id", O.PrimaryKey)
	def name = column[String]("name")
	def price = column[Double]("price")

	def * = (id, name, price)
}

object SampleApp extends App {
	val db = Database.forConfig("sample")

	val products = TableQuery[Products]

	try {
		val q = for {
			c <- products if c.price > 3000.0
		} yield (c.id, c.name)

		db.run(q.result).map(_.foreach(println))

		val insert = DBIO.seq {
			products += ((0, "slicksample01", 100.0))
		}

		val res = db.run(insert)

		res onSuccess {
			case msg => println(msg)
		}

	} finally db.close
}