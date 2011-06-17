
import javax.servlet.http.HttpServletRequest

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.websocket.WebSocket
import org.eclipse.jetty.websocket.WebSocket.Connection
import org.eclipse.jetty.websocket.WebSocketHandler

class EchoWebSocket implements WebSocket.OnTextMessage {
	def outbound

	void onOpen(Connection outbound) {
		this.outbound = outbound
	}

	void onMessage(String data) {
		this.outbound.sendMessage("echo: ${data}")
	}

	void onClose(int closeCode, String message) {
	}
}

def server = new Server(8080)

def wshandler = new WebSocketHandler() {
	WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
		new EchoWebSocket()
	}
}

server.handler = wshandler

server.start()
server.join()

