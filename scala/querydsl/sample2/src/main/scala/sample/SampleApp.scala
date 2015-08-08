package sample

import java.util.Properties
import java.sql.DriverManager

import com.querydsl.sql.dml.SQLInsertClause
import com.querydsl.sql.{SQLQuery, MySQLTemplates}

import sample.model.{QProduct, QProductVariation}

import scala.collection.JavaConversions._

object SampleApp extends App {
	val conf = new Properties()
	conf.load(getClass.getClassLoader.getResourceAsStream("db.properties"))

	val con = DriverManager.getConnection(conf.getProperty("url"), conf)
	con.setAutoCommit(false)

	val templates = new MySQLTemplates()

	val p = QProduct as "p"
	val v = QProductVariation as "v"

	val pid: Long = new SQLInsertClause(con, templates, p)
		.set(p.name, s"sample${System.currentTimeMillis()}")
		.set(p.price, 1500L)
		.executeWithKey(p.id)

	new SQLInsertClause(con, templates, v)
		.set(v.productId, pid).set(v.color, "Green").set(v.size, "L").addBatch()
		.set(v.productId, pid).set(v.color, "Blue").set(v.size, "S").addBatch()
		.execute()

	con.commit()

	val query = new SQLQuery(con, templates)

	val res = query.from(p)
		.join(v).on(v.productId.eq(p.id))
		.where(p.price.between(1300, 2500))
    .select(p.id, p.name, p.price, v.color, v.size)
		.fetch()

	val groupedRes = res.groupBy(x => (x.get(p.id), x.get(p.name), x.get(p.price)))

	println(groupedRes)

	con.close()
}
