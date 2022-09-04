
type CartId = String
type ItemId = String
type Quantity = Int

case class CartItem(itemId: ItemId, qty: Quantity)

enum Cart:
  case Nothing
  case Empty(val id: CartId)
  case Active(val id: CartId, val items: List[CartItem])

enum Command:
  case Create(val id: CartId)
  case ChangeQty(val itemId: ItemId, val qty: Quantity)
  case CancelItem(val itemId: ItemId)

enum Event:
  case Created(val id: CartId)
  case QtyChanged(val id: CartId, val itemId: ItemId, val newQty: Quantity)
  case ItemCancelled(val id: CartId, val itemId: ItemId)

trait Action[S, C, E]:
  extension (state: S)
    def action(command: C): (S, List[E])

trait Restore[S, E]:
  extension (state: S)
    def restore(event: E): Option[S]

import Cart.*
import Command.*
import Event.*

object CartRestore:
  import scala.collection.immutable.ListMap

  given Restore[Cart, Event] with
    extension (state: Cart)
      def restore(event: Event) = state match
        case Nothing => event match
          case Created(id) => Some(Empty(id))
          case _ => None
        case Empty(id) => event match
          case QtyChanged(cid, itemId, qty) if id == cid && qty > 0 =>
            Some(Active(id, List(CartItem(itemId, qty))))
          case _ => None
        case Active(id, items) => event match
          case QtyChanged(cid, itemId, qty) if cid == id && qty > 0 =>
            val gs = groupBy(items)

            gs.find((i, q) => i == itemId && q == qty) match
              case Some(_) => None
              case None =>
                val newItems = (gs + ( (itemId, qty) )).map { (i, q) =>
                  CartItem(i, q)
                }.toList

                if newItems.isEmpty then
                  Some(Empty(id))
                else
                  Some(Active(id, newItems))
          case ItemCancelled(cid, itemId) if cid == id =>
            val newItems = items.filterNot(i => i.itemId == itemId)

            if newItems.isEmpty then
              Some(Empty(id))
            else if newItems.length != items.length then
              Some(Active(id, newItems))
            else
              None
          case _ => None

  private def groupBy(items: List[CartItem]): ListMap[ItemId, Quantity] =
    items.foldLeft(ListMap.empty[ItemId, Quantity]) { (acc, item) =>
      if item.qty > 0 then
        acc + ( (item.itemId, item.qty + acc.getOrElse(item.itemId, 0)) )
      else
        acc
    }
end CartRestore

object CartAction:
  import CartRestore.given

  given Action[Cart, Command, Event] with
    extension (state: Cart)
      def action(cmd: Command) = cmd match
        case Create(id) =>
          restoreCart(state, Created(id))
        case ChangeQty(itemId, qty) if qty > 0 => state match
          case Empty(id) =>
            restoreCart(state, QtyChanged(id, itemId, qty))
          case Active(id, _) =>
            restoreCart(state, QtyChanged(id, itemId, qty))
          case _ => noChange(state)
        case CancelItem(itemId) => state match
          case Active(id, _) => restoreCart(state, ItemCancelled(id, itemId))
          case _ => noChange(state)
        case _ => noChange(state)

  private def noChange(state: Cart) = (state, List.empty)

  private def restoreCart(state: Cart, event: Event) =
    state.restore(event)
      .map(s => (s, List(event)))
      .getOrElse(noChange(state))

end CartAction

@main def main(): Unit =
  import CartAction.given

  val (d1, ev1) = Nothing.action(Create("cart-1"))
  println(s"state = $d1, events = $ev1")

  val (d2, ev2) = d1.action(ChangeQty("item-1", 1))
  println(s"state = $d2, events = $ev2")

  val (d3, ev3) = d2.action(ChangeQty("item-2", 1))
  println(s"state = $d3, events = $ev3")

  val (d4, ev4) = d3.action(ChangeQty("item-1", 3))
  println(s"state = $d4, events = $ev4")

  val (d5, ev5) = d4.action(ChangeQty("item-1", 3))
  println(s"state = $d5, events = $ev5")

  val (d6, ev6) = d5.action(ChangeQty("item-3", 2))
  println(s"state = $d6, events = $ev6")

  val (d7, ev7) = d6.action(CancelItem("item-2"))
  println(s"state = $d7, events = $ev7")

  val (d8, ev8) = d7.action(CancelItem("item-1"))
  println(s"state = $d8, events = $ev8")

  val (d9, ev9) = d8.action(CancelItem("item-2"))
  println(s"state = $d9, events = $ev9")

  val (d10, ev10) = d9.action(CancelItem("item-3"))
  println(s"state = $d10, events = $ev10")

  println("-----")

  val c0 = Active("cart-2", List(CartItem("item-1", 1), CartItem("item-2", 0), CartItem("item-1", 2)))

  val (c1, evc1) = c0.action(ChangeQty("item-1", 3))
  println(s"state = $c1, events = $evc1")

  val (c2, evc2) = c1.action(ChangeQty("item-2", 0))
  println(s"state = $c2, events = $evc2")

  val (c3, evc3) = c2.action(ChangeQty("item-1", 10))
  println(s"state = $c3, events = $evc3")
