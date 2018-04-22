package sample

import cats.free.Free

import sample.repository.InventoryOpF

package object free {
  type InventoryOp[A] = Free[InventoryOpF, A]
}
