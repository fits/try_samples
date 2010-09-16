package fits.sample

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SimpleAdapter
import android.widget.ListView
import android.widget.Toast

import scala.collection.JavaConversions._
import scala.io.Source

import org.json._

/**
 * 指定の DB に含まれるテーブルをリスト表示するクラス
 */
class DbActivity extends ListActivity {

	override def onCreate(savedInstanceState: Bundle) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.db)

		val extras = getIntent().getExtras()

		if (extras != null) {
			val db = extras.getString("DB")

			setTitle(db)
			loadJson(getResources().getString(R.string.table_url) + db)
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

	private def loadJson(url: String) {
		val proc: Option[JSONArray] => Unit = {
			case Some(json) =>
				val dbList = for (i <- 0 until json.length()) 
					yield toMap(json.optJSONObject(i))

				val adapter = new SimpleAdapter(this, dbList, R.layout.item, Array("table_name"), Array(R.id.name))
				
				setListAdapter(adapter)

			case None =>
		}

		new JsonLoadTask(proc).execute(url)
	}

	/**
	 * JSONObject を java.util.Map に変換する
	 */
	private def toMap(jsonObj: JSONObject): java.util.Map[String, String] = {
		val result = new java.util.HashMap[String, String]()

		for (k <- jsonObj.keys) {
			k match {
				case key: String => result.put(key, jsonObj.optString(key))
			}
		}

		result
	}

	/** 
	 * java.util.Map を Bundle に変換する
	 */
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
