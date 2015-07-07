package sample

import java.sql.DriverManager

import com.querydsl.sql.{SQLQuery, MySQLTemplates}
import sample.model.QProduct

object SampleApp extends App{
  val con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sample?user=root")
  val templates = new MySQLTemplates()

  val p = QProduct as "product"

  val query = new SQLQuery(con, templates)

  val res = query.from(p).where(p.price.between(500, 2500))
    .select(p.name).fetch()

  println(res)

  con.close()
}
