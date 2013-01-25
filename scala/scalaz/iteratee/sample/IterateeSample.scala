package fits.sample

import scalaz._
import Scalaz._
import iteratee._

object IterateeSample extends App {
	val enumerator = Iteratee.enumList[Int, Id] { (1 to 5).toList }

	(Iteratee.take[Int, List](3) &= enumerator).run |> println

}
