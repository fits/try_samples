package sample;

import java.util.function.Supplier;

import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.server.HttpServer;

public class HttpSample2 {
	public static void main(String... args) {

		HttpServer<String, String> server = RxNetty.<String, String>newHttpServerBuilder(8080, (req, res) -> {

			System.out.println("----- req -----");
			println("path", req::getPath);
			println("uri", req::getUri);
			println("queryString", req::getQueryString);
			System.out.println("");

			return res.writeStringAndFlush("sample2");
		}).build();

		System.out.println("*** sample2 start");

		server.startAndWait();
	}

	private static <T> void println(String field, Supplier<T> func) {
		System.out.println(field + " : " + func.get());
	}
}