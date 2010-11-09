package sample.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("message")
public class SimpleResource {

	@GET
	@Produces("text/plain")
	public String getMessage() {
		return "ÇƒÇ∑Ç∆ÇﬂÇ¡ÇπÅ[Ç∂";
	}

	@GET
	@Path("{id}")
	@Produces("text/plain")
	public String find(@PathParam("id") String id) {
		return "test" + id;
	}
}