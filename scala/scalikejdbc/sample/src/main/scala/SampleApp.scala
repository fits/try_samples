package sample

import scalikejdbc._
import scalikejdbc.config._

case class Product(id: Long, name: String, price: Double)

object Product extends SQLSyntaxSupport[Product] {
	override val tableName = "product"

	def apply(p: ResultName[Product])(rs: WrappedResultSet) =
		new Product(rs.long(p.id), rs.string(p.name), rs.double(p.price))
}

object SampleApp extends App {
	DBs.setupAll()

	try {
		val price = 3000.0

		val res = DB readOnly { implicit session =>
			sql"select name from product where price > ${price}".map(rs => rs.string("name")).list.apply()
		}

		println(res)

		val p = Product syntax "p"

		val res2 = DB readOnly { implicit session =>
			withSQL {
				select.from(Product as p).where.gt(p.price, price)
			}.map(Product(p.resultName)).list.apply()
		}

		println(res2)

	} finally DBs.closeAll()
}