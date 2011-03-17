package fits.sample

import org.specs2.mutable._
import org.specs2.runner._

import org.junit.runner._

@RunWith(classOf[JUnitRunner])
class SampleTest extends Specification {

	"Sample" should {
		"Sample can set name, get name" in {
			val s = new Sample()

			s.setName("sample")
			s.getName() must be equalTo("sample")
		}
	}
}