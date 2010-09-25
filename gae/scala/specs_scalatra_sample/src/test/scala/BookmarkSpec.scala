package fits.sample

import org.specs._

import com.google.appengine.api.datastore.dev.LocalDatastoreService
import com.google.appengine.tools.development.testing.LocalServiceTestHelper

class BookmarkSpec extends Specification {
	val helper = new LocalServiceTestHelper()
	val testerId = "TESTER01"

	def before(){
		helper.setUp()

		//DataStore へデータを保存しないようにする設定
		LocalServiceTestHelper.getApiProxyLocal().setProperty(LocalDatastoreService.NO_STORAGE_PROPERTY, "true")
	}

	def after(){
		helper.tearDown()
	}

	"BookmarkEntry is empty" in {
		before()

		Bookmark.getEntryList(testerId).length must_==0

		after()
	}

	"can add BookmarkEntry" in {
		before()

		Bookmark.addEntry(testerId, BookmarkEntry("http://localhost/", "default"))
		val list = Bookmark.getEntryList(testerId).toList
		list must haveSize(1)

		val entry = list.head
		entry.url must beEqual("http://localhost/")
		entry.description must beEqual("default")

		after()
	}
}

