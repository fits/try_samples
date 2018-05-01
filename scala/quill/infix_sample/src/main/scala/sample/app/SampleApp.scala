package sample.app

import io.getquill.{H2JdbcContext, UpperCase}

case class Data(id: String, value: Int)

object SampleApp extends App {
  lazy val ctx = new H2JdbcContext(UpperCase, "ctx")

  import ctx._

  val selectData = quote(
    infix"""SELECT ID AS "_1", VALUE AS "_2" FROM DATA""".as[Query[(String, Int)]]
  )

  val p = for {
    _ <- runIO( query[Data].insert(lift(Data("id1", 1))) )
    _ <- runIO( query[Data].insert(lift(Data("id2", 2))) )
    _ <- runIO( query[Data].insert(lift(Data("id3", 3))) )
    r <- runIO( selectData )
  } yield r

  println( performIO(p) )
}
