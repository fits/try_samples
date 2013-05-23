package fits.sample

import scalaz._
import Scalaz._
import Free._

object FreeSample extends App {

	println( return_[Id, Int](10) )
	println( produce(2) )

	(produce(2) >>= { _ => produce(3) } ) |> println

	val a = for {
		x <- return_[Id, Int](10)
		y <- return_[Id, Int](2)
	} yield (x + y)

	println(a)
}

