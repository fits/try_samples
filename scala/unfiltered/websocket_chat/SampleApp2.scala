package fits.sample

import unfiltered.request.Path
import unfiltered.netty._
import unfiltered.netty.websockets._
import akka.actor.ActorSystem
import akka.agent.Agent

object SampleApp2 extends Application {

	implicit val system = ActorSystem("sample")

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

	Http.local(8080).handler(wsHandle).beforeStop {
		// Agent の終了処理
		// ただし、beforeStop は Ctrl + C で終了すると実行されない
		wsList.close()
	}.start()
}
