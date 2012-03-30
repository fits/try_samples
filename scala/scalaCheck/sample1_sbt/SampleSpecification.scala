package fits.sample

import org.scalacheck._
import org.scalacheck.Prop._
import org.scalacheck.Gen._

object SampleSpecification extends Properties("Sample") {
	property("startsWith") = forAll {(a: String, b: String) => 
		(a + b).startsWith(a)
	}

	property("num choose") = forAll(choose(0, 10), choose(5, 9)) {(a: Int, b: Int) =>
		a + b < 20
	}
}
