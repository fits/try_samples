package controllers;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import akka.actor.Cancellable;
import akka.util.FiniteDuration;

import play.*;
import play.libs.Akka;
import play.libs.F.*;
import play.mvc.*;
import play.mvc.WebSocket;

import views.html.*;

public class Application extends Controller {
	private static AtomicLong counter = new AtomicLong();
	private static ConcurrentHashMap<Long, Cancellable> jobList = new ConcurrentHashMap<>();
	private static List<WebSocket.Out<String>> clientList = new CopyOnWriteArrayList<>();

	public static Result index() {
		return ok(index.render());
	}

	public static Result add(String title) {
		final long jobNo = counter.getAndIncrement();

		Cancellable cl = Akka.system().scheduler().scheduleOnce(
			new FiniteDuration(20, "seconds"),
			new Runnable() {
				public void run() {
					System.out.println("start sleep : " + jobNo);
					sendMessage("start sleep : " + jobNo);

					try {
						Thread.currentThread().sleep(10000);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					System.out.println("end sleep : " + jobNo);
					sendMessage("end sleep : " + jobNo);
					jobList.remove(jobNo);
				}
			}
		);
		jobList.put(jobNo, cl);

		return ok("job size = " + jobList.size());
	}

	public static Result cancel(Long jobNo) {
		Cancellable cl = jobList.get(jobNo);
		if (cl != null) {
			cl.cancel();
			jobList.remove(jobNo);
			System.out.println("*** job cancelled : " + jobNo);
		}

		return ok();
	}
	
	public static WebSocket<String> connect() {
		return new WebSocket<String>() {
			public void onReady(WebSocket.In<String> wsin,
					final WebSocket.Out<String> wsout) {

				clientList.add(wsout);

				wsin.onMessage(new Callback<String>() {
					public void invoke(String arg0) throws Throwable {
						System.out.println("recieve : " + arg0);
					}
				});

				wsin.onClose(new Callback0() {
					public void invoke() throws Throwable {
						System.out.println("**** wsin close");
						clientList.remove(wsout);
						wsout.close();
					}
				});
			}
		};
	}

	private static void sendMessage(String msg) {
		for (WebSocket.Out<String> wsout: clientList) {
			wsout.write(msg);
		}
	}
}