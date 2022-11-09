import cats.syntax.all._

case class Item(name: String, value: Int)

@main def sample(): Unit =
  val d1 = Item("item-1", 1).asRight[String]
  val d2 = "nothing".asLeft[Item]

  println(d1)
  println(d2)

  val d3 = d1.map(it => Item(it.name, it.value + 1))

  println(d3)
