@Grab('io.netty:netty-all:4.0.6.Final')
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.HttpVersion
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.util.CharsetUtil
import static io.netty.handler.codec.http.HttpHeaders.Names.*

class SampleHandler extends ChannelInboundHandlerAdapter {
	@Override
	void channelRead(ChannelHandlerContext ctx, Object msg) {
		println "read: ${msg}"

		if (msg instanceof HttpRequest) {
			def res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer('test data', CharsetUtil.US_ASCII))

			res.headers().set(CONTENT_TYPE, 'text/plain')
			res.headers().set(CONTENT_LENGTH, res.content().readableBytes())

			ctx.write(res).addListener(ChannelFutureListener.CLOSE)
		}
	}

	@Override
	void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush()
	}
}

class SampleChannelInitializer extends ChannelInitializer {
	@Override
	void initChannel(Channel ch) {
		def pipeline = ch.pipeline()

		pipeline.addLast('decoder', new HttpRequestDecoder())
		pipeline.addLast('encoder', new HttpResponseEncoder())
		pipeline.addLast('handler', new SampleHandler())
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
