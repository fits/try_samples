package fits.sample

import scala.util.parsing.json.{JSON, JSONObject}

import org.mashupbots.socko.events.HttpRequestEvent
import org.mashupbots.socko.routes.{Routes, GET, POST}
import org.mashupbots.socko.webserver.{WebServer, WebServerConfig}

import akka.actor.{Actor, ActorSystem, Props}

object SampleApp extends App {
	class UserGetHandler extends Actor {
		def receive = {
			case req: HttpRequestEvent =>
				val path = req.request.endPoint.path
				val id = path.replace("/user/", "").split("/").head

				req.response.write(
					JSONObject(
						Map(
							"id" -> id,
							"name" -> "sample"
						)
					).toString(), 
					"application/json"
				)
				context.stop(self)
		}
	}

	class UserPostHandler extends Actor {
		def receive = {
			case req: HttpRequestEvent =>
				val content = req.request.content.toString
				val data = JSON.parseFull(content)

				data.foreach(println)

				req.response.write("")
				context.stop(self)
		}
	}

	val actorSystem = ActorSystem("SampleActorSystem")

	val routes = Routes({
		case GET(req) => actorSystem.actorOf(Props[UserGetHandler]) ! req

		case POST(req) if req.endPoint.path == "/user" =>
			actorSystem.actorOf(Props[UserPostHandler]) ! req
	})

	val server = new WebServer(WebServerConfig(port = 8080), routes, actorSystem)
	server.start()

	Runtime.getRuntime.addShutdownHook(new Thread() {
		override def run { server.stop() }
	})
}
