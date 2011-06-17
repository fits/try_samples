
import java.net.InetSocketAddress
import java.util.concurrent.Executors

import org.jboss.netty.bootstrap.ServerBootstrap
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.channel.*
import org.jboss.netty.handler.codec.http.*
import org.jboss.netty.util.CharsetUtil

//HTTPリクエストの処理クラス
class SampleHttpRequestHandler extends SimpleChannelUpstreamHandler {
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		def req = e.message as HttpRequest

		println("message received : ${req}")

		if (req.method != HttpMethod.GET) {
			sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED)
			return
		}

		def res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
		res.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8")
		res.setContent(ChannelBuffers.copiedBuffer("テスト", CharsetUtil.UTF_8))

		def future = e.channel.write(res)
		//接続を切断するために以下が必要
		future.addListener(ChannelFutureListener.CLOSE)
	}

	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		e.cause.printStackTrace()
		e.channel.close()
	}
}

def server = new ServerBootstrap(new NioServerSocketChannelFactory(
	Executors.newCachedThreadPool(),
	Executors.newCachedThreadPool()
))

//パイプラインの設定
server.setPipelineFactory({
	def pipeline = Channels.pipeline()

	pipeline.addLast("decoder", new HttpRequestDecoder())
	pipeline.addLast("encoder", new HttpResponseEncoder())
	pipeline.addLast("handler", new SampleHttpRequestHandler())

	pipeline
} as ChannelPipelineFactory)

server.bind(new InetSocketAddress(8080))
