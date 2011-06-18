
import java.net.InetSocketAddress
import java.security.MessageDigest
import java.util.concurrent.Executors

import static org.jboss.netty.handler.codec.http.HttpHeaders.*

import org.jboss.netty.bootstrap.ServerBootstrap
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.channel.*
import org.jboss.netty.handler.codec.http.*
import org.jboss.netty.handler.codec.http.websocket.*

import org.jboss.netty.channel.Channels

class ChatServerHandler extends SimpleChannelUpstreamHandler {
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		def msg = e.message

		println("message received : ${msg}")

		handleRequest(ctx, msg)
	}

	//WebSocket ハンドシェイク処理
	def handleRequest(ChannelHandlerContext ctx, HttpRequest req) {

		//ハンドシェイクのレスポンス作成
		def res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, 
				new HttpResponseStatus(101, "Web Socket Protocol Handshake"))

		res.addHeader(Names.UPGRADE, Values.WEBSOCKET)
		res.addHeader(Names.CONNECTION, Values.UPGRADE)

		res.addHeader(Names.SEC_WEBSOCKET_ORIGIN, req.getHeader(Names.ORIGIN))
		res.addHeader(Names.SEC_WEBSOCKET_LOCATION, "ws://localhost:8080/sample")

		def key1 = req.getHeader(Names.SEC_WEBSOCKET_KEY1)
		def key2 = req.getHeader(Names.SEC_WEBSOCKET_KEY2)

		//キー内の数値のみを取り出し数値化したものをキー内の空白数で割る
		int key1res = (int)Long.parseLong(key1.replaceAll("[^0-9]", "")) / key1.replaceAll("[^ ]", "").length()
		int key2res = (int)Long.parseLong(key2.replaceAll("[^0-9]", "")) / key2.replaceAll("[^ ]", "").length()

		long content = req.content.readLong()

		def input = ChannelBuffers.buffer(16)
		input.writeInt(key1res)
		input.writeInt(key2res)
		input.writeLong(content)

		res.content = ChannelBuffers.wrappedBuffer(MessageDigest.getInstance("MD5").digest(input.array))

		ctx.channel.write(res)

		//接続をアップグレード
		def pipeline = ctx.channel.pipeline
		pipeline.replace("decoder", "wsdecoder", new WebSocketFrameDecoder())
		pipeline.replace("encoder", "wsencoder", new WebSocketFrameEncoder())
	}

	//WebSocket 処理
	def handleRequest(ChannelHandlerContext ctx, WebSocketFrame msg) {
		ctx.channel.write(new DefaultWebSocketFrame("echo : ${msg.textData}"))
	}
}

def server = new ServerBootstrap(new NioServerSocketChannelFactory(
	Executors.newCachedThreadPool(),
	Executors.newCachedThreadPool()
))

server.setPipelineFactory({
	def pipeline = Channels.pipeline()

	pipeline.addLast("decoder", new HttpRequestDecoder())
	pipeline.addLast("encoder", new HttpResponseEncoder())
	pipeline.addLast("handler", new ChatServerHandler())

	pipeline
} as ChannelPipelineFactory)

server.bind(new InetSocketAddress(8080))
