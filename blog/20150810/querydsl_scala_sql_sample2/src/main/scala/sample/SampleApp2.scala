package sample

import java.util.Properties
import javax.sql.DataSource

import com.querydsl.scala.sql.SQL
import com.querydsl.sql.{SQLTemplates, MySQLTemplates}

import org.apache.commons.dbcp2.BasicDataSourceFactory

import sample.model.{Product, ProductVariation, QProduct, QProductVariation}

import scala.collection.JavaConversions._

case class QueryDSLHelper(dataSource: DataSource, templates: SQLTemplates) extends SQL

object SampleApp2 extends App {
	val product = (name: String, price: Long) => {
		val res = new Product()
		res.name = name
		res.price = price
		res
	}

	val variation = (productId: Long, color: String, size: String) => {
		val res = new ProductVariation()
		res.productId = productId
		res.color = color
		res.size = size
		res
	}

	val conf = new Properties()
	conf.load(getClass.getClassLoader.getResourceAsStream("db.properties"))

	val dataSource = BasicDataSourceFactory.createDataSource(conf)
	val qdsl = QueryDSLHelper(dataSource, new MySQLTemplates())

	val p = QProduct as "p"
	val v = QProductVariation as "v"

	qdsl.tx {
		val pid = qdsl.insert(p)
			.populate(product(s"test${System.currentTimeMillis()}", 2000L))
			.executeWithKey(p.id)

		qdsl.insert(v)
			.populate(variation(pid, "Red", "M")).addBatch()
			.populate(variation(pid, "Yellow", "F")).addBatch()
			.execute()
	}

	qdsl.tx {
		val res = qdsl.from(p)
			.join(v).on(v.productId.eq(p.id))
			.where(p.price.between(1300, 2500))
			.select(p.id, p.name, p.price, v.color, v.size)
			.fetch()

		val groupedRes = res.groupBy(x => (x.get(p.id), x.get(p.name), x.get(p.price)))

		println(groupedRes)
	}
}
