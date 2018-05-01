package sample.app

import io.getquill.{H2JdbcContext, SnakeCase}

case class Data(id: String, value: Int)

object SampleApp extends App {
  lazy val ctx = new H2JdbcContext(SnakeCase, "ctx")

  import ctx._

  val d1 = quote(query[Data].insert(lift(Data("id1", 1))))
  println( run(d1) )

  println( run(query[Data]) )

  val insertData = (d: Data) => quote(query[Data].insert(lift(d)))

  val p = for {
    _ <- runIO(insertData(Data("id2", 2)))
    _ <- runIO(insertData(Data("id3", 3)))
    r <- runIO(query[Data])
  } yield r

  println( performIO(p) )
}
