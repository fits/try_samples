package fits.sample

import play.api.libs.iteratee._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

import java.io.{BufferedReader, FileReader}

object SampleApp3 extends App {
	import scala.concurrent.ExecutionContext.Implicits.global

	def fromStreamLine(input: BufferedReader) = {
		Enumerator.fromCallback1(_ => {
			val chunk = input.readLine() match {
				case line: String => Some(line)
				case _            => None
			}
			Future.successful(chunk)
		}, {
			input.close
			println("*** close")
		})
	}

	val enumerator = fromStreamLine(new BufferedReader(new FileReader(args(0))))

	val future = enumerator |>>> Iteratee.foreach { s => 
		println(s"#${s}")
	}

	Await.ready(future, Duration.Inf)

	println("----------")

	val enumerator2 = fromStreamLine(new BufferedReader(new FileReader(args(0))))
	val future2 = enumerator2 &> Enumeratee.drop(1) &> Enumeratee.take(2) |>>> Iteratee.foreach { s => 
		println(s"#${s}")
	}

	Await.ready(future2, Duration.Inf)

}
