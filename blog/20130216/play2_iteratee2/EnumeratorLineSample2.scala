package fits.sample

import play.api.libs.iteratee._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

import java.io.{BufferedReader, FileInputStream, InputStreamReader}
import java.nio.charset.StandardCharsets._

object EnumeratorLineSample2 extends App {
	import scala.concurrent.ExecutionContext.Implicits.global

	def fromStreamLine(input: BufferedReader) = {
		Enumerator.fromCallback1(_ => Future {
			input.readLine match {
				case line: String => Some(line)
				case _            => None
			}
		}, {
			println("*** close")
			input.close
		})
	}

/*  // 以下でも可
	def fromStreamLine(input: BufferedReader) = {
		Enumerator.fromCallback1(_ => {
			val chunk = input.readLine match {
				case line: String => Some(line)
				case _            => None
			}
			Future.successful(chunk)
		}, {
			println("*** close")
			input.close
		})
	}
*/

	val enumerator = fromStreamLine(new BufferedReader(new InputStreamReader(new FileInputStream(args(0)), UTF_8)))

	val future = enumerator |>>> Iteratee.foreach { s => 
		println(s"#${s}")
	}

	Await.ready(future, Duration.Inf)

	println("----------")

	val enumerator2 = fromStreamLine(new BufferedReader(new InputStreamReader(new FileInputStream(args(0)), UTF_8)))

	val future2 = enumerator2 &> Enumeratee.drop(1) &> Enumeratee.take(2) |>>> Iteratee.foreach { s => 
		println(s"#${s}")
	}

	Await.ready(future2, Duration.Inf)

	println("----------")

	// ファイルはクローズ済みのため下記では何も出力されない
	val future3 = enumerator2 &> Enumeratee.take(1) |>>> Iteratee.foreach { s => 
		println(s"#${s}")
	}

	Await.ready(future3, Duration.Inf)
}
