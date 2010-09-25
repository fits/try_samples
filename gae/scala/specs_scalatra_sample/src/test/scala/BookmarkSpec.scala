package fits.sample

import org.specs._

import com.google.appengine.api.datastore.dev.LocalDatastoreService
import com.google.appengine.tools.development.testing.LocalServiceTestHelper

class BookmarkSpec extends Specification {
	val helper = new LocalServiceTestHelper()
	val testerId = "TESTER01"

	//Spec毎の前処理
	doBeforeSpec {
		helper.setUp()

		//DataStore へデータを保存しないようにする設定
		LocalServiceTestHelper.getApiProxyLocal().setProperty(LocalDatastoreService.NO_STORAGE_PROPERTY, "true")
	}

	"BookmarkEntry is empty" in {
		Bookmark.getEntryList(testerId).length must_==0
	}

	"can add BookmarkEntry" in {
		Bookmark.addEntry(testerId, BookmarkEntry("http://localhost/", "default"))
		val list = Bookmark.getEntryList(testerId).toList
		list must haveSize(1)

		val entry = list.head
		entry.url must beEqual("http://localhost/")
		entry.description must beEqual("default")
	}

	//Spec毎の後処理
	doAfterSpec {
		helper.tearDown()
	}
}

