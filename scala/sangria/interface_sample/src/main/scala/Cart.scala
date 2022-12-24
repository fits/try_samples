
case class CartItem(item: String, qty: Int)

trait HasId {
  val id: String
}

trait HasItems {
  val items: List[CartItem]
}

enum Cart extends HasId:
  case Empty(id: String)
  case Active(id: String, items: List[CartItem]) extends Cart, HasItems
