package fits.sample

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SimpleAdapter
import android.widget.ListView
import android.widget.Toast;

import scala.collection.JavaConversions._
import scala.io.Source

import org.json._

class DbActivity extends ListActivity {

	override def onCreate(savedInstanceState: Bundle) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.db)

		val extras = getIntent().getExtras()

		if (extras != null) {
			val db = extras.getCharSequence("DB")

			setTitle(db)
			loadTables(db)
		}
	}

	//リストアイテムをクリックした際の処理
	override def onListItemClick(l: ListView, v: View, p: Int, id: Long) {

		val intent = new Intent(this, classOf[TableActivity])
		//Bundle に暗黙の型変換
		val selectedItem: Bundle = l.getItemAtPosition(p)

		intent.putExtra("TABLE", selectedItem)

		startActivity(intent)
	}


	private def loadTables(db: CharSequence) {
		try {
			val url = getResources().getString(R.string.table_url) + db

			val json = Source.fromURL(url).mkString

			val jsonList = new JSONArray(json)

			val dbList = for (i <- 0 until jsonList.length()) 
				yield toMap(jsonList.optJSONObject(i))

			val adapter = new SimpleAdapter(this, dbList, R.layout.item, Array("table_name"), Array(R.id.name))
			setListAdapter(adapter)
		}
		catch {
			case e: Exception => 
				Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
		}
	}

	private def toMap(jsonObj: JSONObject): java.util.Map[String, String] = {
		val result = new java.util.HashMap[String, String]()

		for (k <- jsonObj.keys) {
			k match {
				case key: String => result.put(key, jsonObj.optString(key))
			}
		}

		result
	}

	private implicit def toBundle(o: Object): Bundle = {
		val result = new Bundle()

		o match {
			case m: java.util.Map[String, String] =>
				for((k, v) <- m) {
					result.putString(k, v)
				}
		}

		result
	}
}
