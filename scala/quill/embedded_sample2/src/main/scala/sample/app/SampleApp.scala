package sample.app

import io.getquill.{Embedded, H2JdbcContext, Literal}

case class User(name: String) extends Embedded
case class Data(id: String, value: Int, user: User)

object SampleApp extends App {
  lazy val ctx = new H2JdbcContext(Literal, "ctx")

  import ctx._

  val queryData = quote(querySchema[Data]("SAMPLE_DATA", _.user.name -> "user_name"))

  val d1 = quote(queryData.insert(lift(Data("id1", 1, User("user1")))))
  println( run(d1) )

  println( run(queryData) )

  val insertDataList = (ds: List[Data]) => quote(liftQuery(ds).foreach(queryData.insert(_)))

  val find = quote {
    for {
      r <- queryData if r.user.name == "user1"
    } yield r
  }

  val res = for {
    _ <- runIO(insertDataList(List(Data("id2", 2, User("user2")), Data("id3", 3, User("user1")))))
    r <- runIO(find)
  } yield r

  println( performIO(res) )
}
