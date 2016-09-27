
import io.finch._
import com.twitter.finagle.Http
import com.twitter.util.{ Await, Promise }

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }

class SampleActor extends Actor {
	override def receive = {
		case msg => {
			println(s"receive: ${msg}")
			sender ! s"###${msg}###"
		}
	}
}

class RequestActor(val target: ActorRef, 
	val message: String, val promise: Promise[String]) extends Actor {

	target ! message

	override def receive = {
		case res: String => {
			promise.setValue(res)
			context.stop(self)
		}
	}
}

object SampleApp extends App {
	implicit val system = ActorSystem("sample1")

	val actor = system.actorOf(Props[SampleActor])

	// "/sample/xxx"
	val api = get("sample" :: string).mapOutputAsync { s: String =>
		val p = new Promise[String]

		system.actorOf(Props(classOf[RequestActor], actor, s, p))

		p.map(Ok)
	}

	val server = Http.server.serve(":8080", api.toServiceAs[Text.Plain])

	Await.ready(server)
}