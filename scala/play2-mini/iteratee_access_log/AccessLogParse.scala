package fits.sample

import play.api.libs.iteratee._
import java.io.{FileReader, BufferedReader}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

import AccessLogObject._

object AccessLogParse extends App {
	import scala.concurrent.ExecutionContext.Implicits.global

	val reader = new BufferedReader(new FileReader(args(0)))

	val enumerator = Enumerator.generateM( Future {
		reader.readLine() match {
			case line: String => Some(line)
			case _ => None
		}
	})

	val parseLog: Enumeratee[String, Option[AccessLog]] = Enumeratee.collect {
		case AccessLogPattern(h, l, u, t, r, s, b, rf, ua) =>
			val req = r match {
				case RequestLogPattern(m, p, pr) => Some(RequestLog(m, p, pr))
				case _ => None
			}
			Some(AccessLog(h, t, req, s.toInt, b.toLong, rf, ua))

		case _ => None
	}

	val future = enumerator &> Enumeratee.take(3) &> parseLog  |>>> Iteratee.foreach { 
		_ foreach(println)
	}

	Await.ready(future, Duration.Inf)

	reader.close()
}
