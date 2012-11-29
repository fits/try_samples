package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

import org.codehaus.jackson.JsonNode;

public class Application extends Controller {

	public static Result index() {
		return ok(index.render());
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result send() {
		System.out.println("--- queryString ---");
		System.out.println(request().queryString());

		System.out.println("--- body ---");
		JsonNode json = request().body().asJson();
		System.out.println(json);

		return ok();
	}
  
}