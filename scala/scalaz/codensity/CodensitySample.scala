package fits.sample

import scalaz._
import Scalaz._

object CodensitySample extends App {

	Codensity.pureCodensity(5).apply { (x: Int) => () => println(x) }()
}
