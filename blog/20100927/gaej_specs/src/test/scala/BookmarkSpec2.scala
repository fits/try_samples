package fits.sample

import org.specs._
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig

class BookmarkSpec2 extends Specification {
	val helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig)
	val testerId = "TESTER01"

	"ブックマーク" should {
		//doBefore と doAfter は Example（"XXXX" in {・・・}）毎に実施される
		doBefore {
			println("--- doBefore")
			helper.setUp()
		}

		doAfter {
			helper.tearDown()
		}

		"初回のブックマークは空" in {
			Bookmark.getEntryList(testerId).length must_==0
		}

		"ブックマーク追加" in {
			Bookmark.addEntry(testerId, BookmarkEntry("http://localhost/", "default"))
			val list = Bookmark.getEntryList(testerId).toList
			list must haveSize(1)

			val entry = list.head
			entry.url must beEqual("http://localhost/")
			entry.description must beEqual("default")
		}

	}
}

