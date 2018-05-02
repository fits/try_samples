package sample

import io.getquill.{H2JdbcContext, SnakeCase}

case class Item(itemId: String, name: String)
case class Stock(stockId: String, itemId: String, qty: Int)

object SampleApp extends App {
  lazy val ctx = new H2JdbcContext(SnakeCase, "ctx")

  import ctx._

  val r1 = probe("CREATE TABLE item(item_id VARCHAR(10) PRIMARY KEY, name VARCHAR(10))")
  println(s"create table1: $r1")

  val r2 = executeAction("CREATE TABLE stock(stock_id VARCHAR(10) PRIMARY KEY, item_id VARCHAR(10), qty INT)")
  println(s"create table2: $r2")

  println( run(query[Item].insert(lift(Item("item1", "A1")))) )
  println( run(query[Item].insert(lift(Item("item2", "B2")))) )

  println( run(query[Stock].insert(lift(Stock("stock1", "item1", 5)))) )
  println( run(query[Stock].insert(lift(Stock("stock2", "item2", 3)))) )

  println( run(query[Item]) )
  println( run(query[Stock]) )

  val selectStocks = quote(
    infix"""SELECT stock_id AS "_1", name AS "_2", qty AS "_3"
           FROM stock s join item i on i.item_id = s.item_id""".as[Query[(String, String, Int)]]
  )

  println( run(selectStocks) )
}
