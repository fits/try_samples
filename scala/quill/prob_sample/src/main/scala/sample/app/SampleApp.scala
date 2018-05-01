package sample.app

import io.getquill.{H2JdbcContext, UpperCase}

case class Data(id: String, value: Int)

object SampleApp extends App {
  lazy val ctx = new H2JdbcContext(UpperCase, "ctx")

  import ctx._

  val r = probe("CREATE TABLE DATA(ID VARCHAR(10), VALUE INT)")

  println(s"create table: $r")

  val p = for {
    _ <- runIO( query[Data].insert(lift(Data("id1", 1))) )
    _ <- runIO( query[Data].insert(lift(Data("id2", 2))) )
    r <- runIO( query[Data] )
  } yield r

  println( performIO(p) )
}
