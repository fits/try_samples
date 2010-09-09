package fits.sample

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class SimpleActivity extends Activity {
	override def onCreate(savedInstanceState: Bundle) {
		super.onCreate(savedInstanceState)

		setContentView(new TextView(this) {
			setText("hello")
		})
	}
}