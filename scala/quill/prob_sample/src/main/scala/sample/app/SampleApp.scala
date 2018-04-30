package sample.app

import io.getquill.{H2JdbcContext, UpperCase}

case class Data(id: String, value: Int)

object SampleApp extends App {
  lazy val ctx = new H2JdbcContext(UpperCase, "ctx")

  import ctx._

  val r = probe("CREATE TABLE DATA(id VARCHAR(10), value int)")

  println(s"create table: $r")

  val p = for {
    _ <- runIO( quote(query[Data].insert(lift(Data("id1", 1)))) )
    _ <- runIO( quote(query[Data].insert(lift(Data("id2", 2)))) )
    r <- runIO( quote(query[Data]) )
  } yield r

  println( performIO(p) )
}
