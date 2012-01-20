package fits.sample;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("sample")
public class SampleResource {

	@POST
	@Path("data")
	@Produces("application/xml")
	public Data data(@FormParam("name") String name) {
		Data result = new Data();
		result.name = name;
		return result;
	}

	@GET
	@POST
	@Path("{other}")
	public Response other() {
		ErrorData err = new ErrorData();
		err.code = "001";
		err.message = "invalid path";

		return Response.status(400).entity(err).build();
	}

}
