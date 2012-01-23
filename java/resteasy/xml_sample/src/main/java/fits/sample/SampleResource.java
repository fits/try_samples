package fits.sample;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.validation.constraints.*;

import org.jboss.resteasy.spi.validation.ValidateRequest;

@Path("sample")
@ValidateRequest
public class SampleResource {

	@POST
	@Path("data")
	@Produces("application/xml")
	public Data data(
		@FormParam("name")
		@NotNull
		@Size(max=10)
		String name) {

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
