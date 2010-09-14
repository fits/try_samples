package fits.sample

import android.app.Activity
import android.os.Bundle
import android.view.View

class ItemDetailActivity extends TypedActivity {
	override def onCreate(savedInstanceState: Bundle) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.item_detail)

		val extras = getIntent().getExtras()

		if (extras != null) {
			findView(TR.item_name).setText(extras.getCharSequence("ITEM"))
			findView(TR.item_content).setText("テストデータ")
		}
	}

}

