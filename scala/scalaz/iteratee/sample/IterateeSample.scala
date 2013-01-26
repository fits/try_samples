package fits.sample

import scalaz._
import Scalaz._
import iteratee._
import Iteratee._

object IterateeSample extends App {
	val enumerator = Iteratee.enumList[Int, Id] { (1 to 10).toList }

	(take[Int, List](3) &= enumerator).run |> println

	(collect[List[Int], List] %= group(2) &= enumerator).run |> println
}
