
import sangria.schema.*
import sangria.execution.Executor
import sangria.macros.graphql
import sangria.marshalling.circe.*

import scala.concurrent.ExecutionContext

case class Store(list: List[Cart]):
  def findCart(id: String): Option[Cart] = list.find(_.id == id)

@main def main(): Unit =
  val CartItemType = ObjectType("CartItem", fields[Unit, CartItem](
    Field("item", IDType, resolve = _.value.item),
    Field("qty", IntType, resolve = _.value.qty)
  ))

  val CartInterfaceType = InterfaceType("Cart", fields[Unit, HasId](
    Field("id", IDType, resolve = _.value.id)
  ))

  val ItemsInterfaceType = InterfaceType("Items", fields[Unit, HasItems](
    Field("items", ListType(CartItemType), resolve = _.value.items)
  ))

  val EmptyCartType = ObjectType(
    "EmptyCart",
    interfaces(CartInterfaceType),
    fields[Unit, Cart.Empty](
      Field("id", IDType, resolve = _.value.id)
    )
  )

  val ActiveCartType = ObjectType(
    "ActiveCart",
    interfaces(CartInterfaceType, ItemsInterfaceType),
    fields[Unit, Cart.Active](
      Field("id", IDType, resolve = _.value.id),
      Field("items", ListType(CartItemType), resolve = _.value.items)
    )
  )

  val QueryType = ObjectType("Query", fields[Store, Unit](
    Field(
      "find", 
      OptionType(CartInterfaceType),
      arguments = Argument("id", IDType) :: Nil,
      resolve = c => c.ctx.findCart(c.args.arg[String]("id"))
    )
  ))

  val schema = Schema(QueryType, additionalTypes = List(EmptyCartType, ActiveCartType))

  given executionContext: ExecutionContext = ExecutionContext.global

  val store = Store(List(
    Cart.Empty("cart-1"),
    Cart.Active("cart-2", List(CartItem("item-1", 1), CartItem("item-2", 2)))
  ))

  val q1 = graphql"""
    {
      find(id: "cart-1") {
        __typename
        id
        ... on Items {
          items {
            item
            qty
          }
        }
      }
    }
  """

  val q2 = graphql"""
    {
      find(id: "cart-2") {
        __typename
        id
        ... on Items {
          items {
            item
            qty
          }
        }
      }
    }
  """

  val q3 = graphql"""
    {
      find(id: "cart-3") {
        __typename
        id
      }
    }
  """

  List(q1, q2, q3).foreach { q =>
    val r = Executor.execute(schema, q, store)
    r.foreach(println(_))
  }


