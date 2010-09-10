package fits.sample

import android.app.ListActivity
import android.os.Bundle
import android.widget.ArrayAdapter

class SampleListActivity extends ListActivity {
	override def onCreate(savedInstanceState: Bundle) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.main)

		val adapter = new ArrayAdapter(this, R.layout.item, R.id.name, Array("20100903_Sinatra風PHP用フレームワークLimonadeによるWebアプリケーション作成", "20100906_F#でASP.NET", "20100909_ScalaでAndroidアプリケーション作成 - sbt使用"))

		setListAdapter(adapter)
	}
}

