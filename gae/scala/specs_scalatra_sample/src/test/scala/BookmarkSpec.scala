package fits.sample

import org.specs._
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig

class BookmarkSpec extends Specification {
	val helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig)
	val testerId = "TESTER01"
	lazy val setUp = helper.setUp()
	lazy val tearDown = helper.tearDown()

	"初期状態" should {
		doBefore(setUp)
		doAfter(tearDown)

		"ブックマークは空" in {
			Bookmark.getEntryList(testerId).length must_==0
		}
	}

	"ブックマーク追加" should {
		var list: List[BookmarkEntry] = List()

		//doBefore と doAfter は Example（"XXXX" in {・・・}）毎に実施される
		doBefore {
			setUp

			Bookmark.addEntry(testerId, BookmarkEntry("http://localhost/", "default"))
			list = Bookmark.getEntryList(testerId).toList
		}

		doAfter(tearDown)

		"サイズは 1" in {
			list must haveSize(1)
		}

		"ブックマークは http://localhost/ で default" in {
			val entry = list.head
			entry.url must beEqual("http://localhost/")
			entry.description must beEqual("default")
		}
	}
}

