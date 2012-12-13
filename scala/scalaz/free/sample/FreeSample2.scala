package fits.sample

import scalaz._
import Scalaz._
import Free._

object FreeSample2 extends App {

	trait Greeting[A]
	case class Hello[A](n: A) extends Greeting[A]
	case class Bye[A]() extends Greeting[A]

	trait GreetingInstances {
		implicit val convInstance = new Functor[Greeting] {
			def map[A, B](fa: Greeting[A])(f: A => B): Greeting[B] = {
				fa match {
					case Hello(n) => Hello(f(n))
					case _ => Bye()
				}
			}
		}
	}

	case object Greeting extends GreetingInstances

	def hello[A](a: A): Free[({type f[+x] = Greeting[x]})#f, Unit] = {
		Suspend[({type f[+x] = Greeting[x]})#f, Unit](
			Functor[Greeting].map(Hello(a)) {
				Return[({type f[+x] = Greeting[x]})#f, Unit](_)
			}
		)
	}

	def bye[A]: Free[({type f[+x] = Greeting[x]})#f, Unit] = {
		Suspend[({type f[+x] = Greeting[x]})#f, Unit](
			Functor[Greeting].map(Bye()) {
				Return[({type f[+x] = Greeting[x]})#f, Unit](_)
			}
		)
	}

	println(hello("aaaa"))

	val c = for {
		_ <- hello("1")
		_ <- hello("2")
		_ <- hello("3")
		_ <- bye
	} yield ()

	println(c)
}
