package sample.service.interpreter

import java.util.UUID

import cats.data.Kleisli
import sample.model._
import sample.repository.StockRepository
import sample.service.StockService

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object common {
  type StockOp[A] = Kleisli[Future, StockRepository[Stock, StockId], A]
}

import common._

class StockServiceInterpreter extends StockService[StockOp, Stock, StockId, ItemId, Quantity] {

  private def toOp[A](f: StockRepository[Stock, StockId] => Try[A]): StockOp[A] =
    Kleisli { repo =>
      Future.fromTry(f(repo))
    }

  override def current(stockId: StockId): StockOp[Option[Stock]] = toOp(_.find(stockId))

  override def regist(itemId: ItemId, managed: Boolean): StockOp[Stock] =
    toOp { repo =>
      val stockId: StockId = UUID.randomUUID().toString
      val stock = if (managed) ManagedStock(stockId, itemId) else UnmanagedStock(stockId, itemId)

      repo.save(stock)
    }

  override def move(from: StockId, to: StockId, qty: Quantity): StockOp[(Stock, Stock)] =
    toOp { repo =>
      for {
        f <- repo.find(from)
        t <- repo.find(to)
        _ <- isSameItem(f, t)
        nf <- out(f, qty)
        nt <- in(t, qty)
        _ <- repo.save(nf)
        _ <- repo.save(nt)
      } yield (nf, nt)
    }

  def isSameItem(from: Option[Stock], to: Option[Stock]): Try[Boolean] = {
    val res = for {
      f <- from
      t <- to
    } yield f.itemId == t.itemId

    if (res.getOrElse(false))
      Success(true)
    else
      Failure(new Error)
  }

  private def in(s: Option[Stock], qty: Quantity): Try[Stock] = s match {
    case Some(a) => a match {
      case m: ManagedStock => Success(m.copy(in = m.in + qty))
      case u: UnmanagedStock => Success(u.copy(in = u.in + qty))
    }
    case _ => Failure(new Error)
  }

  private def out(s: Option[Stock], qty: Quantity): Try[Stock] = s match {
    case Some(a) => a match {
      case m: ManagedStock if m.qty >= qty => Success(m.copy(out = m.out + qty))
      case u: UnmanagedStock => Success(u.copy(out = u.out + qty))
      case _ => Failure(new Error(s"current qty <= $qty"))
    }
    case _ => Failure(new Error)
  }
}
