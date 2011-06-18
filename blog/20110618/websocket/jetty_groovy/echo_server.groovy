
import javax.servlet.http.HttpServletRequest

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.websocket.WebSocket
import org.eclipse.jetty.websocket.WebSocket.Connection
import org.eclipse.jetty.websocket.WebSocketHandler

class EchoWebSocket implements WebSocket.OnTextMessage {
	def outbound

	void onOpen(Connection outbound) {
		println("onopen : ${this}")
		this.outbound = outbound
	}

	void onMessage(String data) {
		println("onmessage : ${this} - ${data}")
		this.outbound.sendMessage("echo: ${data}")
	}

	void onClose(int closeCode, String message) {
		println("onclose : ${this} - ${closeCode}, ${message}")
	}
}

def server = new Server(8080)
//WebSocketópÇÃ Handler Çê›íË
server.handler = new WebSocketHandler() {
	WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
		println("websocket connect : ${protocol} - ${request}")
		new EchoWebSocket()
	}
}

server.start()
server.join()

