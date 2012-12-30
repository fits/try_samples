package fits.sample

import org.mashupbots.socko.events.HttpRequestEvent
import org.mashupbots.socko.routes.{Routes, GET}
import org.mashupbots.socko.webserver.{WebServer, WebServerConfig}

import akka.actor.{Actor, ActorSystem, Props}

object SampleApp extends App {
	class SampleHandler extends Actor {
		def receive = {
			case req: HttpRequestEvent =>
				req.response.write("sample")
				context.stop(self)
		}
	}

	val actorSystem = ActorSystem("SampleActorSystem")

	val routes = Routes({
		case GET(req) => actorSystem.actorOf(Props[SampleHandler]) ! req
	})

	val server = new WebServer(WebServerConfig(), routes, actorSystem)
	server.start()

	Runtime.getRuntime.addShutdownHook(new Thread() {
		override def run { server.stop() }
	})
}
