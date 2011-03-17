package fits.sample

import org.specs2.mutable._
import org.specs2.runner._

import org.junit.runner._

/** 
 * Maven + JUnit4 で実行する場合 Test で終わるクラス名にするか、
 * mvn コマンドの実行時に "-Dtest=クラス名" で指定する必要がある
 * 
 * （例）mvn -Dtest=*Spec test
 */
@RunWith(classOf[JUnitRunner])
class SampleSpecTest extends Specification {

	"Sample" should {
		"Sample can set name, get name" in {
			val s = new Sample()

			s.setName("sample")
			s.getName() must be equalTo("sample")
		}
	}
}

