package fits.sample

import android.app.ListActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast;

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

	private def loadTables(db: CharSequence) {
		try {
			val url = "http://169.254.118.149:4567/tables/" + db
			val json = Source.fromURL(url).mkString

			val obj = new JSONArray(json)

			val dbList = for (i <- 0 until obj.length()) 
				yield obj.optJSONObject(i).getString("table_name")

			val adapter = new ArrayAdapter(this, R.layout.item, R.id.name, dbList.toArray)
			setListAdapter(adapter)
		}
		catch {
			case e: Exception => 
				Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
		}
	}

}

