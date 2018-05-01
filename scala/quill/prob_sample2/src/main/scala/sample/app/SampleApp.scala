package sample.app

import io.getquill.{H2JdbcContext, UpperCase}

case class Data(id: String, value: Int)

object SampleApp extends App {
  lazy val ctx = new H2JdbcContext(UpperCase, "ctx")

  import ctx._

  val p = for {
    r1 <- IO.fromTry(probe("CREATE TABLE DATA(ID VARCHAR(10), VALUE INT)"))
    r2 <- runIO( query[Data].insert(lift(Data("id1", 1))) )
    r3 <- runIO( query[Data].insert(lift(Data("id2", 2))) )
    r4 <- runIO( query[Data] )
  } yield (r1, r2, r3, r4)

  println( performIO(p) )
}
