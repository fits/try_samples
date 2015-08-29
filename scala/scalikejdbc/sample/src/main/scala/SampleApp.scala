package sample

import scalikejdbc._
import scalikejdbc.config._

object SampleApp extends App {
	DBs.setupAll()

	try {
		val price = 3000.0

		val res = DB readOnly { implicit session =>
			sql"select name from product where price > ${price}".map(rs => rs.string("name")).list.apply()
		}

		println(res)

	} finally DBs.closeAll()
}