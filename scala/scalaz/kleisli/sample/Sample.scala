package fits.sample

import scalaz._
import Scalaz._

object Sample extends App {

	val plus = (x: Int) => some(x + 3)
	val times = (x: Int) => some(x * 2)

	// Some(14)
	println(4 |> plus >>= times)

	// Some(14)
	println(some(4) >>= plus >>= times)

	// Some(14)
	println(some(4) >>= List(Kleisli(plus), Kleisli(times)).reduceLeft {(b, a) =>
		b >=> a
	})
	// Some(14)
	println(some(4) >>= List(Kleisli(plus), Kleisli(times)).reduceRight {(a, b) =>
		b <=< a
	})

	// Some(11)
	println(some(4) >>= List(Kleisli(plus), Kleisli(times)).reduceLeft {(b, a) =>
		b <=< a
	})
	// Some(11)
	println(some(4) >>= List(Kleisli(plus), Kleisli(times)).reduceRight {(a, b) =>
		b >=> a
	})


}
