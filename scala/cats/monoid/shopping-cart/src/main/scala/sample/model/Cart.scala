package sample.model

import cats.Monoid
import cats.data.NonEmptyList

object common {
  type CartId = String
  type ItemId = String
  type Amount = BigDecimal
  type Quantity = Int
}

import common._

trait Summary[A] {
  def total(implicit m: Monoid[A]): A
}

trait Cart {
  val id: CartId
}

trait BasicCart extends Cart with Summary[Amount] {
  val items: NonEmptyList[CartItem]

  def total(implicit m: Monoid[Amount]): Amount =
    items.map(_.total).foldLeft(m.empty)(m.combine)
}

case class CartItem(id: ItemId, price: Amount, quantity: Quantity) extends Summary[Amount] {
  def total(implicit m: Monoid[Amount]): Amount = price * quantity
}

case class EmptyCart(id: CartId) extends Cart
case class NonEmptyCart(id: CartId, items: NonEmptyList[CartItem]) extends BasicCart
