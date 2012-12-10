package fits.sample

import scalaz._
import Scalaz._

object Sample extends App {

	sealed trait Greeting[A]
	case class Hello[A](n: A) extends Greeting[A]
	case class Bye[A]() extends Greeting[A]

	trait GreetingInstances {
		implicit val greetingInstance = new Functor[Greeting] {
			def map[A, B](fa: Greeting[A])(f: A => B): Greeting[B] = {
				fa match {
					case Hello(n) => Hello(f(n))
					case _ => Bye()
				}
			}
		}
	}

	case object Greeting extends GreetingInstances

	Hello("a") |> println
	Bye() |> println

	Functor[Greeting].map(Hello("a")) { (a) => a + "!!" } |> println
	Functor[Greeting].map(Bye()) { _ => println("no call") } |> println
}
