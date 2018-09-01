package sample

import cats.arrow.FunctionK
import cats.free.Free
import cats.syntax.either._
import cats.~>

import scala.collection.mutable

case class Stock(id: String, qty: Int = 0)

object command {
  trait Command[A]

  case class Create(id: String) extends Command[Stock]
  case class Add(id: String, qty: Int) extends Command[Stock]
  case class Remove(id: String, qty: Int) extends Command[Stock]

  type CommandOp[A] = Free[Command, A]

  def create(id: String): CommandOp[Stock] = Free.liftF(Create(id))
  def add(id: String, qty: Int): CommandOp[Stock] = Free.liftF(Add(id, qty))
  def remove(id: String, qty: Int): CommandOp[Stock] = Free.liftF(Remove(id, qty))
}

object event {
  trait Event

  case class Created(id: String) extends Event
  case class Added(qty: Int) extends Event
  case class Removed(qty: Int) extends Event
}

object common {
  type EitherS[A] = Either[String, A]
}

import command._
import event._
import common._

object MemoryEventStore {
  private lazy val store = mutable.Map.empty[String, List[Event]]

  def events(id: String): EitherS[List[Event]] =
    Either.right(store.getOrElse(id, List.empty[Event]))

  def save(id: String, event: Event): EitherS[Event] =
    events(id).map { es =>
      store.put(id, es :+ event)
      event
    }
}

object Interpreter {

  import cats.implicits._
  import MemoryEventStore._

  def interpret[A](c: CommandOp[A]): EitherS[A] = c.foldMap(step)

  val step: FunctionK[Command, EitherS] = new (Command ~> EitherS) {
    override def apply[A](fa: Command[A]): EitherS[A] = fa match {
      case Create(id) => for {
        es <- events(id)
        _ <- validateEmpty(es)
        r <- save(id, Created(id))
        s <- restore(List(r))
      } yield s
      case Add(id, qty) => for {
        es <- events(id)
        _ <- restore(es)
        r <- save(id, Added(qty))
        s <- restore(es :+ r)
      } yield s
      case Remove(id, qty) => for {
        es <- events(id)
        s <- restore(es)
        _ <- validateQty(s, qty)
        r <- save(id, Removed(qty))
        s <- restore(es :+ r)
      } yield s
    }
  }

  private def restore(es: List[Event]): EitherS[Stock] =
    if (es.isEmpty)
      Either.left("empty event")
    else
      Either.right {
        es.foldLeft(Stock("")) { (acc, ev) =>
          ev match {
            case Created(id) => Stock(id)
            case Added(qty) => Stock(acc.id, acc.qty + qty)
            case Removed(qty) => Stock(acc.id, acc.qty - qty)
          }
        }
      }

  private def validateEmpty(es: List[Event]): EitherS[Unit] =
    if (es.isEmpty) Either.right(()) else Either.left("not empty")

  private def validateQty(s: Stock, qty: Int): EitherS[Unit] =
    if (s.qty >= qty) Either.right(()) else Either.left(s"stock shortage(< $qty)")
}

object SampleApp extends App {
  import Interpreter._

  val id1 = "id1"
  val id2 = "id2"

  val p1 = for {
    _ <- create(id1)
    _ <- create(id2)
    _ <- add(id1, 5)
    a <- add(id1, 3)
    _ <- add(id2, 10)
    b <- remove(id2, 1)
  } yield (a, b)

  val r1 = interpret(p1)
  println(r1)

  println(MemoryEventStore.events(id1))

  val p2 = for {
    _ <- remove(id1, 2)
    _ <- remove(id1, 2)
    r <- remove(id1, 5)
  } yield r

  val r2 = interpret(p2)
  println(r2)

  println(MemoryEventStore.events(id1))
}

