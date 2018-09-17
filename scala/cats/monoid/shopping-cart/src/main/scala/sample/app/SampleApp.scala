package sample.app

import cats.implicits._
import cats.data.NonEmptyList

import sample.model.{CartItem, NonEmptyCart}

object SampleApp extends App {

  val items = NonEmptyList.of(
    CartItem("item1", 100, 2),
    CartItem("item2", 1000, 3)
  )

  val cart1 = NonEmptyCart("cart1", items)

  println(cart1)
  println(cart1.total)
}
