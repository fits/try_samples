package sample.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/rest")
public class SampleResource {

	@GET
	@Path("sample")
	public String sample() {
		return "サンプル";
	}
}
