package sample.app

import io.getquill.{Embedded, H2JdbcContext, SnakeCase}

case class User(name: String) extends Embedded
case class Data(id: String, value: Int, user: User)

object SampleApp extends App {
  lazy val ctx = new H2JdbcContext(SnakeCase, "ctx")

  import ctx._

  val d1 = quote(query[Data].insert(lift(Data("id1", 1, User("user1")))))
  println( run(d1) )

  println( run(query[Data]) )

  val insertData = (d: Data) => quote(query[Data].insert(lift(d)))

  val p = for {
    _ <- runIO(insertData(Data("id2", 2, User("user2"))))
    _ <- runIO(insertData(Data("id3", 3, User("user1"))))
    r <- runIO(query[Data].filter(d => d.user.name == "user1"))
  } yield r

  println( performIO(p) )
}
