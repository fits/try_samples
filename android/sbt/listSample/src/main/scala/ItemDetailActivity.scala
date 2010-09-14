package fits.sample

import android.app.Activity
import android.os.Bundle
import android.view.View

class ItemDetailActivity extends Activity {
	override def onCreate(savedInstanceState: Bundle) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.item_detail)

		val extras = getIntent().getExtras()

		if (extras != null) {
			
		}
	}

}

