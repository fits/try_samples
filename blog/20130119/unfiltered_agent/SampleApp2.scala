package fits.sample

import unfiltered.request.Path
import unfiltered.netty._
import unfiltered.netty.websockets._
import akka.actor.ActorSystem
import akka.agent.Agent

object SampleApp2 extends Application {

	implicit val system = ActorSystem()

	// WebSocket のリスト
	val wsList = Agent(List[WebSocket]())

	val wsHandle = Planify {
		case Path("/connect") => {
			case Open(socket) => wsList.send( _ :+ socket ) // リストへの追加

			case Close(socket) =>
				// リストから削除
				wsList.send {
					_.filterNot( _ == socket )
				}
				println("*** closed")

			case Message(socket, Text(txt)) => 
				// リストを取得してメッセージ送信
				wsList().foreach( _.send(txt) )

			case Error(socket, err) => println(s"error : ${err}")
		}
	}

	scala.sys.ShutdownHookThread {
		// Agent の終了処理
		wsList.close()
	}

	Http.local(8080).handler(wsHandle).start()
}
