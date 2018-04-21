package sample.app

import cats.data.Writer
import cats.implicits._
import cats.free.Free
import cats.~>

import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

sealed trait DataOpF[A]

case class Data(id: String, value: Int = 0)

case class Store(data: Data) extends DataOpF[Unit]
case class Find(id: String) extends DataOpF[Data]

object common {
  type DataOp[A] = Free[DataOpF, A]
}

import common._

trait DataRepository {
  def store(id: String, value: Int): DataOp[Unit] = Free.liftF(Store(Data(id, value)))
  def find(id: String): DataOp[Data] = Free.liftF(Find(id))
}

object DataRepository extends DataRepository

case class DataRepoInterpreter() {
  private lazy val repo = mutable.Map.empty[String, Data]

  val step: DataOpF ~> Future = new (DataOpF ~> Future) {
    override def apply[A](fa: DataOpF[A]): Future[A] = fa match {
      case Store(d) => Future {
        repo += ((d.id, d))
        ()
      }
      case Find(id) => Future(repo.getOrElse(id, Data(id)))
    }
  }

  def interpret[A](op: DataOp[A]): Future[A] = op.foldMap(step)
}

object DataLogger {
  type WriterList[A] = Writer[List[String], A]

  val step: DataOpF ~> WriterList = new (DataOpF ~> WriterList) {
    override def apply[A](fa: DataOpF[A]): WriterList[A] = fa match {
      case Store(d) => Writer(List(s"store:$d"), ())
      case Find(id) => Writer(List(s"find:$id"), Data(id))
    }
  }

  def interpret[A](op: DataOp[A]): WriterList[A] = op.foldMap(step)
}

object SampleApp extends App {
  val steps: DataOp[(Data, Data, Data)] = for {
    _ <- DataRepository.store("a", 5)
    a <- DataRepository.find("a")
    b <- DataRepository.find("b")
    _ <- DataRepository.store("c", 1)
    _ <- DataRepository.store("c", 2)
    c <- DataRepository.find("c")
  } yield (a, b, c)

  val proc = DataRepoInterpreter()

  val r = proc.interpret(steps)

  r.foreach(msg => println(s"*** $msg"))

  Await.ready(r, 1.seconds)

  val r2 = DataLogger.interpret(steps)

  println(r2)
}
