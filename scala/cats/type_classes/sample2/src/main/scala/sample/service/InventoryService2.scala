package sample.service

import cats.FlatMap
import cats.syntax.functor._, cats.syntax.flatMap._

trait InventoryService2[F[_], ItemId, InventoryItem, Amount] {
  type InventoryOp[A] = F[A]

  def current(id: ItemId): InventoryOp[InventoryItem]
  def add(id: ItemId, quantity: Amount): InventoryOp[InventoryItem]
  def remove(id: ItemId, quantity: Amount): InventoryOp[InventoryItem]

  def move(from: ItemId, to: ItemId, quantity: Amount)
          (implicit Fm: FlatMap[F]): InventoryOp[(InventoryItem, InventoryItem)] =
    for {
      r <- remove(from, quantity)
      a <- add(to, quantity)
    } yield (r, a)
}
