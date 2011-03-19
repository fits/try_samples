package fits.sample

import org.specs._
import org.specs.runner.JUnit4

class BookSpecTest extends JUnit4(BookSpec)

object BookSpec extends Specification {

	"Book に title を指定" should {
		val b = new Book()
		b.setTitle("テスト")

		"title が設定されている" in {
			b.getTitle() must be equalTo("テスト")
		}
	}
}

