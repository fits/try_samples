package fits.sample

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView

import org.json._

/**
 * DB をリスト表示するクラス
 */
class JsonListActivity extends ListActivity {

	override def onCreate(savedInstanceState: Bundle) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.main)

		loadJson(getResources().getString(R.string.db_url))
	}

	//リストアイテムをクリックした際の処理
	override def onListItemClick(l: ListView, v: View, p: Int, id: Long) {
		//クリックしたアイテムを取得
		val selectedItem = l.getItemAtPosition(p)

		val intent = new Intent(this, classOf[DbActivity])
		intent.putExtra("DB", selectedItem.toString())

		startActivity(intent)
	}

	//JSON データを取得する
	private def loadJson(url: String) {
		val proc: Option[JSONArray] => Unit = {
			case Some(json) =>
				val dbList = for (i <- 0 until json.length()) 
					yield json.optJSONObject(i).getString("table_schema")

				val adapter = new ArrayAdapter(this, R.layout.item, R.id.name, dbList.toArray)
				setListAdapter(adapter)

			case None =>
		}

		new JsonLoadTask(proc).execute(url)
	}
}

