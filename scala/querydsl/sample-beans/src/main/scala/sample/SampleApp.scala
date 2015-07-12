package sample

import java.util.Properties
import javax.sql.DataSource

import com.querydsl.scala.sql.SQL
import com.querydsl.sql.{SQLTemplates, MySQLTemplates}
import org.apache.commons.dbcp2.BasicDataSourceFactory
import sample.model.QProduct
import sample.model.Product

case class QueryDSLHelper(dataSource: DataSource, templates: SQLTemplates) extends SQL

object SampleApp extends App{
  val conf = new Properties()
  conf.load(getClass().getClassLoader().getResourceAsStream("db.properties"))

  val dataSource = BasicDataSourceFactory.createDataSource(conf)

  val qdsl = QueryDSLHelper(dataSource, new MySQLTemplates())

  val p = QProduct as "p"

  qdsl.tx {
    val p1 = new Product()
    p1.name = "test" + System.currentTimeMillis()
    p1.price = 1250L

    qdsl.insert(p).populate(p1).executeWithKey(p.id)
  }

  qdsl.tx {
    val res = qdsl.from(p).where(p.price.between(500, 2500))
      .select(p.id, p.name).fetch()

    println(res)
  }
}
