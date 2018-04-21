package sample.app

import cats.arrow.FunctionK
import cats.free.Free
import cats.{Id, ~>}

sealed trait Greeting[A]

case class Hello(message: String) extends Greeting[Unit]
case class Bye() extends Greeting[Unit]

object SampleApp extends App {
  type GreetingFree[A] = Free[Greeting, A]

  def hello(msg: String): GreetingFree[Unit] = Free.liftF(Hello(msg))
  def bye: GreetingFree[Unit] = Free.liftF(Bye())

  def printData: FunctionK[Greeting, Id] = new (Greeting ~> Id) {
    override def apply[A](fa: Greeting[A]): Id[A] = fa match {
      case Hello(msg) =>
        println(s"* $msg")
      case Bye() =>
        println("* bye")
    }
  }

  val messages: GreetingFree[Unit] = for {
    _ <- hello("one")
    _ <- hello("two")
    _ <- hello("three")
    _ <- bye
  } yield ()

  val res = messages.foldMap(printData)

  println(res)
}
