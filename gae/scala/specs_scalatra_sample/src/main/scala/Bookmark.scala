package fits.sample

import scala.collection.JavaConversions._
import com.google.appengine.api.datastore._

case class BookmarkEntry(val url: String, val description: String)

object Bookmark {
	private val bookmarkKind = "Bookmark"
	private val bookmarkEntryKind = "BookmarkEntry"
	private val descriptionProperty = "description"
	private val store = DatastoreServiceFactory.getDatastoreService()

	//BookmarkEntry を追加する
	def addEntry(userId: String, entry: BookmarkEntry) = {
		using(store.beginTransaction()) {tr =>
			val bkey = createBookmarkKey(userId)

			try {
				store.get(bkey)
			}
			catch {
				case e: EntityNotFoundException => 
					store.put(tr, new Entity(bkey))
			}

			val bentry = new Entity(bookmarkEntryKind, entry.url, bkey)
			bentry.setProperty(descriptionProperty, entry.description)

			store.put(tr, bentry)
		}
	}

	//BookmarkEntry を取得する
	def getEntryList(userId: String): Iterator[BookmarkEntry] = {

		val query = new Query(bookmarkEntryKind, createBookmarkKey(userId))

		store.prepare(query).asIterator().map {et =>
			val url: String = et.getKey().getName()
			BookmarkEntry(url, et.getProperty(descriptionProperty))
		}
	}

	private def using(tr: Transaction)(f: Transaction => Unit) = {
		try {
			f(tr)
			tr.commit()
		}
		catch {
			case e: Exception => 
				e.printStackTrace()
				tr.rollback()
		}
	}

	private implicit def toStr(obj: Object): String = {
		obj match {
			case s: String => s
			case _ => ""
		}
	}

	private def createBookmarkKey(userId: String): Key = {
		KeyFactory.createKey(bookmarkKind, userId)
	}
}

