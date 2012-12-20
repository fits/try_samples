package fits.sample

import scalaz._
import Scalaz._

object CodensitySample2 extends App {

	def cont[F[+_]](a: Int) = Codensity.pureCodensity[F, Int](a)

	def calc1[F[+_]](x: Int) = cont[F](x + 3)

	def calc2[F[+_]](x: Int) = cont[F](x * 10)

	def calc3[F[+_]](x: Int) = cont[F](x + 4)

	def calcAll[F[+_]](x: Int) = cont[F](x).flatMap(calc1).flatMap(calc2).flatMap(calc3)

	// a. 2 + 3 = 5
	calc1(2).apply { Option(_) } foreach(println)

	// b. ((2 + 3) * 10) + 4 = 54
	calcAll(2).apply { Option(_) } foreach(println) 

	// c. 54 - 9 = 45
	calcAll(2).apply { (x) => Option(x - 9) } foreach(println)
}
