package sample;

import com.sun.net.httpserver.HttpServer;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		HttpServer server = HttpServerFactory.create("http://localhost:8082/");
		server.start();

		System.out.println("server start ...");
		System.in.read();
		server.stop(0);
	}
}