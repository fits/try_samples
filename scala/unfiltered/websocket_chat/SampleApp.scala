package fits.sample

import unfiltered.request.Path
import unfiltered.netty._
import unfiltered.netty.websockets._
import scala.concurrent.stm._

object SampleApp extends Application {

	val wsList = Ref(List[WebSocket]())

	val wsHandle = Planify {
		case Path("/connect") => {
			case Open(socket) => wsList.single.transform( _ :+ socket )

			case Close(socket) =>
				wsList.single.transform {
					_.filterNot( _ == socket )
				}
				println("*** closed")

			case Message(socket, Text(txt)) => 
				wsList.single.get.foreach( _.send(txt) )


			case Error(socket, err) => println(s"error : ${err}")
		}
	}

	Http.local(8080).handler(wsHandle).start()
}
