package sample

import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class Products(tag: Tag) extends Table[(Int, String, Double)](tag, "product") {
	def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name")
	def price = column[Double]("price")

	def * = (id, name, price)
}

object Products {
	val products = TableQuery[Products]
}

class ProductVariations(tag: Tag) extends Table[(Int, Int, String, String)](tag, "product_variation") {
	def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
	def productId = column[Int]("product_id")
	def color = column[String]("color")
	def size = column[String]("size")

	def * = (id, productId, color, size)

	def product = foreignKey("", productId, Products.products)(_.id)
}

object ProductVariations {
	val productVariations = TableQuery[ProductVariations]
}


object SampleApp extends App {
	val db = Database.forConfig("sample")

	val products = Products.products
	val variations = ProductVariations.productVariations

	try {
		val q = for {
			c <- products if c.price > 3000.0
		} yield (c.id, c.name)

		db.run(q.result).map(_.foreach(println))

		val insert = DBIO.seq {
			val idRes = products.map(p => (p.name, p.price)).returning(products.map(_.id)) += ("slick" + System.currentTimeMillis(), 200.0)

			println(products.map(p => (p.name, p.price)).returning(products.map(_.id)).insertStatement)

			idRes.flatMap {id =>
				variations.map(v => (v.productId, v.color, v.size)) ++= Seq(
					(id, "Cyan", "L"),
					(id, "Black", "M")
				)
			}
		}.transactionally

		val res = db.run(insert)

		Await.result(res, Duration.Inf)

		res foreach println

	} finally db.close
}