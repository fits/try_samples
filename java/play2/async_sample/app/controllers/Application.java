package controllers;

import play.*;
import play.mvc.*;
import play.libs.Akka;

import views.html.*;

import akka.util.FiniteDuration;

public class Application extends Controller {

	public static Result index() {
		return ok(index.render());
	}

	public static Result jobAdd() {
		final Http.Request req = request();

		Akka.system().scheduler().scheduleOnce(
			new FiniteDuration(0, "seconds"),
			new Runnable() {
				public void run() {
					System.out.println("start sleep : " + req);

					try {
						Thread.currentThread().sleep(5000);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					System.out.println("stop sleep : " + req);
				}
			}
		);

		return ok("job added");
	}

}