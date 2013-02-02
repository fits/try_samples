package fits.sample

import scalaz._, Scalaz._
import effect._, IO._
import iteratee._, Iteratee._

object AccessLogParse extends App {

	val LogPattern = """^([^ ]*) ([^ ]*) ([^ ]*) \[([^]]*)\] "(.*?)" ([^ ]*) ([^ ]*) "(.*?)" "(.*?)"""".r

	import java.io._

	val reader = new BufferedReader(new FileReader(args(0)))

	val enumerator = EnumeratorUtil.enumBufferedReader[IO](reader)

	val iter = collect[String, Stream].up[IO] %= map { s: IoExceptionOr[String] =>
		s.valueOr("") match {
			case LogPattern(host, _, _, time, action, status, size, _, ua) => ua
			case _ => ""
		}
	}

	(iter &= enumerator).run.unsafePerformIO().foreach( println )

	IoExceptionOr(reader.close)
}
