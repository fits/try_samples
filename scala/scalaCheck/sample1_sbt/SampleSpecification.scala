package fits.sample

import org.scalacheck._
import org.scalacheck.Prop._


object SampleSpecification extends Properties("String") {
	property("startsWith") = forAll {(a: String, b: String) => 
		(a + b).startsWith(a)
	}
}
