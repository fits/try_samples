package fits.sample

import scalaz._
import Scalaz._

object CodensitySample extends App {

	Codensity.pureCodensity(5).apply { (x: Int) => () => println(x) }()

	Codensity.pureCodensity(5).apply { (x: Int) => List(x) } |> println

	Codensity.rep(List(1, 2)).apply { (x) => List(x, 2, 3)} |> println

	((Codensity.pureCodensity(5).improve >>= { (x: Int) => Codensity.pureCodensity[Function0, Int](x + 3) }) apply { (x) => () => println(x) })()

}
