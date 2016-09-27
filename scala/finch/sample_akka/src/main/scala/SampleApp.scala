
import io.finch._
import com.twitter.finagle.Http
import com.twitter.util.Await
import com.twitter.util.{ Future => TwitterFuture }
import com.twitter.util.Promise

import akka.actor.{ Actor, ActorSystem, Props }
import akka.pattern.ask
import akka.util.Timeout

import scala.language.implicitConversions
import scala.language.postfixOps
import scala.concurrent.duration._
import scala.concurrent.{ Future => ScalaFuture }
import scala.util.{ Success, Failure }

class SampleActor extends Actor {
	override def receive = {
		case msg => {
			println(s"receive: ${msg}")
			sender ! s"###${msg}###"
		}
	}
}

object SampleApp extends App {
	implicit def scalaToTwitter[T](f: ScalaFuture[T]): TwitterFuture[T] = {
		val p = new Promise[T]

		f.onComplete {
			case Success(v) => p.setValue(v)
			case Failure(e) => p.setException(e)
		}

		p
	}

	implicit val system = ActorSystem("sample1")
	implicit val executeContext = system.dispatcher
	implicit val timeout = Timeout(5 seconds)

	lazy val actor = system.actorOf(Props[SampleActor])

	// "/sample/xxx"
	val api = get("sample" :: string).mapOutputAsync { s: String =>
		actor.ask(s).map(r => Ok(r.toString))
	}

	val server = Http.server.serve(":8080", api.toServiceAs[Text.Plain])

	Await.ready(server)
}