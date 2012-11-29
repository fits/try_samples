package fits.sample

import scalaz._
import Scalaz._

object CodensitySample2 extends App {

	def cont[F[+_]](a: Int) = Codensity.pureCodensity[F, Int](a)

	def calc1[F[+_]](x: Int) = cont[F](x + 3)
	def calc2[F[+_]](x: Int) = cont[F](x * 10)

	def calcAll[F[+_]](x: Int) = cont[F](x).flatMap(calc1).flatMap(calc2)

	calc1(2).apply { (a) => () => println(a) }()
	calc1(2).apply { Option(_) } |> println

	calcAll(2).apply { (x) => () => println(x) }()
	calcAll(2).apply { (x) => () => x - 9 }() |> println

}
