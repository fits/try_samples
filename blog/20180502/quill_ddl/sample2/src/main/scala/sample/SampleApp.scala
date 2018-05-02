package sample

import io.getquill.{H2JdbcContext, SnakeCase}

case class Item(itemId: String, name: String)
case class Stock(stockId: String, itemId: String, qty: Int)

object SampleApp extends App {
  lazy val ctx = new H2JdbcContext(SnakeCase, "ctx")

  import ctx._

  val createTables = for {
    it <- probe("CREATE TABLE item(item_id VARCHAR(10) PRIMARY KEY, name VARCHAR(10))")
    st <- probe("CREATE TABLE stock(stock_id VARCHAR(10) PRIMARY KEY, item_id VARCHAR(10), qty INT)")
  } yield (it, st)

  val insertItemAndStock = (itemId: String, name: String, stockId: String, qty: Int) => for {
    _ <- runIO( query[Item].insert(lift(Item(itemId, name))) )
    _ <- runIO( query[Stock].insert(lift(Stock(stockId, itemId, qty))) )
  } yield ()

  val selectStocks = quote {
    for {
      s <- query[Stock]
      i <- query[Item] if i.itemId == s.itemId
    } yield (i, s)
  }

  val proc = for {
    r1 <- IO.fromTry(createTables)
    _ <- insertItemAndStock("item1", "A1", "stock1", 5)
    _ <- insertItemAndStock("item2", "B2", "stock2", 3)
    r2 <- runIO(selectStocks)
  } yield (r1, r2)

  println( performIO(proc) )
  //println( performIO(proc.transactional) )
}
