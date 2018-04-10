package sample.service

import cats.FlatMap
import cats.syntax.functor._, cats.syntax.flatMap._

trait InventoryService3[F[_], ItemId, InventoryItem, Amount] {
  type InventoryOp[A] = F[A]

  implicit val Fm: FlatMap[F]

  def current(id: ItemId): InventoryOp[InventoryItem]
  def add(id: ItemId, quantity: Amount): InventoryOp[InventoryItem]
  def remove(id: ItemId, quantity: Amount): InventoryOp[InventoryItem]

  def move(from: ItemId, to: ItemId, quantity: Amount): InventoryOp[(InventoryItem, InventoryItem)] =
    for {
      r <- remove(from, quantity)
      a <- add(to, quantity)
    } yield (r, a)
}
