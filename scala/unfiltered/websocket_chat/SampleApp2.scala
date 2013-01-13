package fits.sample

import unfiltered.request.Path
import unfiltered.netty._
import unfiltered.netty.websockets._
import akka.actor.ActorSystem
import akka.agent.Agent

object SampleApp2 extends Application {

	implicit val system = ActorSystem("sample")

	val wsList = Agent(List[WebSocket]())

	val wsHandle = Planify {
		case Path("/connect") => {
			case Open(socket) => wsList.send( _ :+ socket )

			case Close(socket) =>
				wsList.send {
					_.filterNot( _ == socket )
				}
				println("*** closed")

			case Message(socket, Text(txt)) => 
				wsList().foreach( _.send(txt) )


			case Error(socket, err) => println(s"error : ${err}")
		}
	}

	Http.local(8080).handler(wsHandle).beforeStop {
		wsList.close()
	}.start()
}
