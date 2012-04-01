package fits.sample

import org.specs2._
import org.scalacheck._

class SampleSpec extends Specification with ScalaCheck { def is = 

	"startsWith" ! check {(a: String, b: String) => (a + b) must startWith(a)} ^	end
}
