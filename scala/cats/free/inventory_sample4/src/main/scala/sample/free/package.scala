package sample

import cats.data.EitherT
import cats.free.Free

import sample.repository.InventoryOpF

package object free {
  type InventoryOpFree[A] = Free[InventoryOpF, A]
  type InventoryOp[A] = EitherT[InventoryOpFree, String, A]
}
