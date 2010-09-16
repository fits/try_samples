package fits.sample

import android.os.Bundle

/**
 * 指定テーブルの詳細を表示するクラス
 */
class TableActivity extends TypedActivity {
	override def onCreate(savedInstanceState: Bundle) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.table)

		val extras = getIntent().getExtras()

		if (extras != null) {
			extras.get("TABLE") match {
				case b: java.util.Map[String, String] =>
					setTitle(b.get("table_name"))

					findView(TR.table_type).setText(b.get("table_type"))
					findView(TR.engine).setText(b.get("engine"))
					findView(TR.avg_row_length).setText(b.get("avg_row_length"))
					findView(TR.create_time).setText(b.get("create_time"))
			}
		}
	}

}

