package fits.sample

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast;

import scala.io.Source

import org.json._

class JsonListActivity extends ListActivity {
	override def onCreate(savedInstanceState: Bundle) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.main)

		try {
			val url = getResources().getString(R.string.db_url)
			val json = Source.fromURL(url).mkString

			val obj = new JSONArray(json)

			val dbList = for (i <- 0 until obj.length()) 
				yield obj.optJSONObject(i).getString("table_schema")

			val adapter = new ArrayAdapter(this, R.layout.item, R.id.name, dbList.toArray)
			setListAdapter(adapter)
		}
		catch {
			case e: Exception => 
				Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
		}
	}

	//リストアイテムをクリックした際の処理
	override def onListItemClick(l: ListView, v: View, p: Int, id: Long) {
		//クリックしたアイテムを取得
		val selectedItem = l.getItemAtPosition(p)

		val intent = new Intent(this, classOf[DbActivity])
		intent.putExtra("DB", selectedItem.toString())

		startActivity(intent)
	}
}

