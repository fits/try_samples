package fits.sample

import play.api.libs.iteratee._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

import java.io.{BufferedReader, FileInputStream, InputStreamReader}
import java.nio.charset.StandardCharsets._

object EnumeratorLineSample extends App {
	import scala.concurrent.ExecutionContext.Implicits.global

	val reader = new BufferedReader(new InputStreamReader(new FileInputStream(args(0)), UTF_8))
	reader.mark(512)

	val enumerator = Enumerator.generateM( Future {
		reader.readLine match {
			case line: String => Some(line)
			case _ => None
		}
	})

	val future = enumerator |>>> Iteratee.foreach { s => 
		println(s"#${s}")
	}

	Await.ready(future, Duration.Inf)

	println("----------")

	reader.reset

	val future2 = enumerator &> Enumeratee.drop(1) &> Enumeratee.take(2) |>>> Iteratee.foreach { s => 
		println(s"#${s}")
	}

	Await.ready(future2, Duration.Inf)

	println("----------")

	val future3 = enumerator &> Enumeratee.take(1) |>>> Iteratee.foreach { s => 
		println(s"#${s}")
	}

	Await.ready(future3, Duration.Inf)

	reader.close
}
