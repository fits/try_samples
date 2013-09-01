@Grab('io.netty:netty-all:4.0.7.Final')
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory

class SampleHandler extends ChannelInboundHandlerAdapter {
	private WebSocketServerHandshaker ws

	@Override
	void channelRead(ChannelHandlerContext ctx, Object msg) {
		println "read: ${msg}"

		if (msg instanceof FullHttpRequest) {
			handleHttp(ctx, msg)
		}
		else if (msg instanceof WebSocketFrame) {
			handleWebSocket(ctx, msg)
		}
	}

	@Override
	void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush()
	}

	private void handleHttp(ChannelHandlerContext ctx, FullHttpRequest req) {
		def factory = new WebSocketServerHandshakerFactory('ws://localhost:8080/sample', null, false)

		ws = factory.newHandshaker(req)

		if (ws == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel())
		}
		else {
			ws.handshake(ctx.channel(), req)
		}
	}

	private void handleWebSocket(ChannelHandlerContext ctx, WebSocketFrame frame) {
		if (frame instanceof CloseWebSocketFrame) {
			ws.close(ctx.channel(), frame.retain())
		}
		else if (!(frame instanceof TextWebSocketFrame)) {
			throw new UnsupportedOperationException("not supported : ${frame.getClass()}")
		}
		else {
			ctx.channel().write(new TextWebSocketFrame("echo : ${frame.text()}"))
		}
	}
}

class SampleChannelInitializer extends ChannelInitializer<SocketChannel> {
	@Override
	void initChannel(SocketChannel ch) {
		ch.pipeline().addLast(
			new HttpServerCodec(),
			new HttpObjectAggregator(65536),
			new SampleHandler()
		)
	}
}

def b = new ServerBootstrap()

def bgroup = new NioEventLoopGroup()
def wgroup = new NioEventLoopGroup()

try {
	b.group(bgroup, wgroup)
		.channel(NioServerSocketChannel)
		.childHandler(new SampleChannelInitializer())

	def ch = b.bind(8080).sync().channel()

	println "start ..."

	ch.closeFuture().sync()

} finally {
	bgroup.shutdownGracefully()
	wgroup.shutdownGracefully()
}