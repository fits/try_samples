package fits.sample

import android.app.Activity
import android.os.Bundle
import android.view.View

class TableActivity extends TypedActivity {
	override def onCreate(savedInstanceState: Bundle) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.table)

		val extras = getIntent().getExtras()

		if (extras != null) {
			val b = extras.getBundle("TABLE")

			setTitle(b.getString("table_name"))

			findView(TR.table_type).setText(b.getString("table_type"))
			findView(TR.engine).setText(b.getString("engine"))
			findView(TR.avg_row_length).setText(b.getString("avg_row_length"))
			findView(TR.create_time).setText(b.getString("create_time"))
		}
	}

}

