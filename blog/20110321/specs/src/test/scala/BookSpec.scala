package fits.sample

import scala.collection.JavaConversions._
import org.specs._

class BookSpec extends SpecificationWithJUnit {

	"初期状態" should {
		val b = new Book()

		"comments は null ではない" in {
			b.getComments() must notBeNull
		}

		"comments は空" in {
			b.getComments() must haveSize(0)
		}
	}

	"Comment を追加" should {
		val b = new Book()
		b.getComments().add(new Comment())

		"Comment が追加されている" in {
			b.getComments() must haveSize(1)
		}
	}
}

