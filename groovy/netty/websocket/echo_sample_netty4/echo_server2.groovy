@Grab('io.netty:netty-all:4.0.7.Final')
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler

class SampleHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
	@Override
	void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
		println "read: ${frame}"
		ctx.channel().writeAndFlush(new TextWebSocketFrame("echo : ${frame.text()}"))
	}
}

class SampleChannelInitializer extends ChannelInitializer<SocketChannel> {
	@Override
	void initChannel(SocketChannel ch) {
		ch.pipeline().addLast(
			new HttpServerCodec(),
			new HttpObjectAggregator(65536),
			new WebSocketServerProtocolHandler("/"),
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