package fits.sample

import scalaz._
import Scalaz._

object CodensitySample2 extends App {

	def cont[F[+_]](a: Int) = Codensity.pureCodensity[F, Int](a)

	def calc1[F[+_]](x: Int) = cont[F](x + 3)
	def calc2[F[+_]](x: Int) = cont[F](x * 10)

	def calcAll[F[+_]](x: Int) = cont[F](x).flatMap(calc1).flatMap(calc2)

	calc1(2).apply { (x) => () => println(x) }()
	calc1(2).apply { Option(_) } |> println
	calc1(2).apply { Option(_) }.println
	calc1(2).apply { Option(_) } foreach(println)


	calc1(2).apply { Value(_) } |> println
	calc1(2).apply { List(_) } |> println

	calcAll(2).apply { (x) => () => println(x) }()
	calcAll(2).apply { (x) => () => x - 9 }() |> println
	calcAll(2).apply { (x) => Option(x - 9) } foreach(println)

}
