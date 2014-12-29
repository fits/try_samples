
import static ratpack.handling.Handlers.*;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.launch.HandlerFactory;
import ratpack.launch.LaunchConfig;

public class AppHandlerFactory implements HandlerFactory {
	@Override
	public Handler create(LaunchConfig config) throws Exception {
		return chain(
			path("sample/:id", ctx -> 
				ctx.render("sample - " + ctx.getPathTokens().get("id")))
		);
	}
}
