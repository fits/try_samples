package sample;

import io.undertow.Undertow;
import lombok.val;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.server.reactive.UndertowHttpHandlerAdapter;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import sample.config.AppConfig;

public class App {
	public static void main(String... args) {

		val ctx = new AnnotationConfigApplicationContext();
		ctx.register(AppConfig.class);
		ctx.refresh();

		val handler = new DispatcherHandler();
		handler.setApplicationContext(ctx);

		val httpHandler = WebHttpHandlerBuilder.webHandler(handler).build();

		val server = Undertow.builder()
				.addHttpListener(8080, "localhost")
				.setHandler(new UndertowHttpHandlerAdapter(httpHandler))
				.build();

		server.start();
	}
}
