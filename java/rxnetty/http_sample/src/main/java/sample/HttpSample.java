package sample;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.server.HttpServer;

public class HttpSample {
	public static void main(String... args) {
		HttpServer<ByteBuf, ByteBuf> server = RxNetty.createHttpServer(8080, (req, res) -> {
			System.out.println(req);
			System.out.println(res);

			return res.writeStringAndFlush("sample");
		});

		server.startAndWait();
	}
}