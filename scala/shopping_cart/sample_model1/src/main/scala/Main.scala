
type CartId = String
type ItemId = String
type Quantity = Int

case class CartItem(itemId: ItemId, qty: Quantity)

enum Cart:
  case None
  case Empty(val id: CartId)
  case Active(val id: CartId, val items: List[CartItem])

enum Command:
  case Create(val id: CartId)
  case AddItem(val itemId: ItemId)
  case UpdateQty(val itemId: ItemId, val qty: Quantity)

enum Event:
  case Created(val id: CartId)
  case ItemAdded(val id: CartId, val itemId: ItemId)
  case QtyUpdated(val id: CartId, val itemId: ItemId, val newQty: Quantity)

trait Action[S, C, E]:
  extension (state: S)
    def action(command: C): (S, List[E])

import Cart.*
import Command.*
import Event.*

object CartAction:
  given Action[Cart, Command, Event] with
    extension (state: Cart)
      def action(cmd: Command) = state match
        case None => cmd match
          case Create(id) => (Empty(id), List(Created(id)))
          case _ => (state, List.empty)
        case Empty(id) => cmd match
          case AddItem(itemId) =>
            (Active(id, List(CartItem(itemId, 1))), List(ItemAdded(id, itemId)))
          case _ => (state, List.empty)
        case Active(id, items) => cmd match
          case AddItem(itemId) if !items.exists(i => i.itemId == itemId) =>
            (Active(id, items :+ CartItem(itemId, 1)), List(ItemAdded(id, itemId)))
          case UpdateQty(itemId, q) =>
            val qty = Math.max(0, q)

            val (newItems, events) = items.foldLeft( (List.empty[CartItem], List.empty[Event]) ) {
              (acc, item) =>
                if item.itemId == itemId && item.qty != qty then
                  (
                    if qty == 0 then acc._1 else acc._1 :+ CartItem(itemId, qty),
                    acc._2 :+ QtyUpdated(id, itemId, qty)
                  )
                else
                  (acc._1 :+ item, acc._2)
            }

            val newState =
              if events.isEmpty then
                state
              else if newItems.isEmpty then
                Empty(id)
              else
                Active(id, newItems)

            (newState, events)
          case _ => (state, List.empty)

@main def main(): Unit =
  import CartAction.given

  val (d1, ev1) = None.action(Create("cart-1"))
  println(s"state = $d1, events = $ev1")

  val (d2, ev2) = d1.action(AddItem("item-1"))
  println(s"state = $d2, events = $ev2")

  val (d3, ev3) = d2.action(AddItem("item-2"))
  println(s"state = $d3, events = $ev3")

  val (d4, ev4) = d3.action(UpdateQty("item-1", 3))
  println(s"state = $d4, events = $ev4")

  val (d5, ev5) = d4.action(UpdateQty("item-1", 3))
  println(s"state = $d5, events = $ev5")

  val (d6, ev6) = d5.action(AddItem("item-3"))
  println(s"state = $d6, events = $ev6")

  val (d7, ev7) = d6.action(UpdateQty("item-2", 0))
  println(s"state = $d7, events = $ev7")

  val (d8, ev8) = d7.action(UpdateQty("item-1", 0))
  println(s"state = $d8, events = $ev8")

  val (d9, ev9) = d8.action(UpdateQty("item-3", 0))
  println(s"state = $d9, events = $ev9")
