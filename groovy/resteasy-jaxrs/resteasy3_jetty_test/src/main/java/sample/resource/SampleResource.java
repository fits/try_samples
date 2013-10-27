package sample.resource;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import sample.annotation.Sample;

@Path("/rest")
public class SampleResource {

	@GET
	@Path("a")
	@Produces(MediaType.APPLICATION_JSON)
	@Sample
	public List<String> a() {
		return Arrays.asList("a1", "a2", "a3");
	}

	@GET
	@Path("b")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> b() {
		return Arrays.asList("b1", "b2");
	}
}
