package fits.sample

import java.text.SimpleDateFormat
import java.util.{Date, Locale}

object AccessLogObject {
	val dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.US)

	case class RequestLog(val method: String, val path: String, val protocol: String)

	case class AccessLog(
		val client: String,
		val time: Date,
		val request: Option[RequestLog],
		val status: Int,
		val size: Long,
		val referer: String,
		val userAgent: String
	)

	// リクエスト内容の正規表現
	val RequestLogPattern = """([^ ]*) ([^ ]*) (.*)""".r

	// ログ内容の正規表現
	val AccessLogPattern = """^([^ ]*) ([^ ]*) ([^ ]*) \[([^]]*)\] "(.*?)" ([^ ]*) ([^ ]*) "(.*?)" "(.*?)"""".r

	implicit def toDate(dateString: String) = dateFormat.parse(dateString)
}
