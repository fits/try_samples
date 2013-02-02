package fits.sample

import scalaz._, Scalaz._
import effect._, IO._
import iteratee._, Iteratee._

import java.io.{BufferedReader, FileReader}
import java.util.{Date, Locale}
import java.text.SimpleDateFormat

object AccessLogParse extends App {
	val dformat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.US)

	// リクエストの内容
	case class RequestLog(val method: String, val path: String, val protocol: String)
	// ログの内容
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

	val reader = new BufferedReader(new FileReader(args(0)))

	val enumerator = EnumeratorUtil.enumBufferedReader[IO](reader)

	val iter = collect[Option[AccessLog], Stream].up[IO] %= map { s: IoExceptionOr[String] =>
		s.valueOr("") match {
			case AccessLogPattern(h, l, u, t, r, s, b, ref, ua) =>
				val req = r match {
					case RequestLogPattern(m, p, pr) => Some(RequestLog(m, p, pr))
					case _ => None
				}

				Some(AccessLog(h, dformat.parse(t), req, s.toInt, b.toLong, ref, ua))
			case _ => None
		}
	}

	(iter &= enumerator).run.unsafePerformIO().foreach {
		_ foreach(println)
	}

	IoExceptionOr(reader.close)
}
