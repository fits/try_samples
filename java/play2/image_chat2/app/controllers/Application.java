package controllers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Callable;

import play.*;
import play.mvc.*;
import play.libs.Akka;
import play.libs.F.*;

import views.html.*;

import akka.util.FiniteDuration;
import org.codehaus.jackson.JsonNode;

public class Application extends Controller {
	private static List<WebSocket.Out<JsonNode>> clientList = new CopyOnWriteArrayList<>();

	public static Result index() {
		return ok(index.render());
	}

	public static WebSocket<JsonNode> connect() {
		return new WebSocket<JsonNode>() {
			public void onReady(WebSocket.In<JsonNode> wsin,
					final WebSocket.Out<JsonNode> wsout) {

				clientList.add(wsout);

				wsin.onMessage(new Callback<JsonNode>() {
					public void invoke(final JsonNode arg0) throws Throwable {
						Akka.future(new Callable<Void>() {
							public Void call() {
								sendMessage(arg0);
								return null;
							}
						});
					}
				});

				wsin.onClose(new Callback0() {
					public void invoke() throws Throwable {
						System.out.println("**** wsin close");
						clientList.remove(wsout);
					}
				});
			}
		};
	}

	private static void sendMessage(JsonNode json) {
		for (WebSocket.Out<JsonNode> wsout: clientList) {
			wsout.write(json);
		}
	}
}