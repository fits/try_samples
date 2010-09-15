package fits.sample

import android.util.Log
import scala.io.Source
import org.json._

/**
 * JSON を非同期的にロードする処理を担うタスククラス
 */
class JsonLoadTask(val proc: Option[JSONArray] => Unit) 
	extends SingleAsyncTask[String, Option[JSONArray]] {

	override def doSingleTask(url: String): Option[JSONArray] = {
		try {
			val json = Source.fromURL(url).mkString
			Some(new JSONArray(json))
		}
		catch {
			case e: Exception => 
				Log.e("json", e.getMessage(), e)
				None
		}
	}

	override def onPostExecute(result: Option[JSONArray]) {
		proc(result)
	}

}
