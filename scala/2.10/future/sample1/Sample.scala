package fits.sample

import scala.concurrent._
import duration.Duration
import ExecutionContext.Implicits.global

object Sample extends App {
	val f: Future[Int] = future {
		Thread.sleep(2000)
		10
	}

	f onComplete {
		// Success(10)
		case x => println(s"res : $x")
	}

	Await.ready(f, Duration.Inf)
}
