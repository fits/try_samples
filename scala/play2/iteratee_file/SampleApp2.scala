package fits.sample

import play.api.libs.iteratee._
import java.io.{FileReader, BufferedReader}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object SampleApp2 extends App {
	import scala.concurrent.ExecutionContext.Implicits.global

	val reader = new BufferedReader(new FileReader(args(0)))

	val enumerator = Enumerator.generateM( Future {
		reader.readLine() match {
			case line: String => Some(line)
			case _ => None
		}
	})

	val f = enumerator |>>> Iteratee.foreach { s => 
		println(s"#${s}")
	}

	Await.ready(f, Duration.Inf)

	reader.close()
}
