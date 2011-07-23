package fits.sample;

import javax.ws.rs.*;
import javax.xml.bind.annotation.XmlRootElement;

@Path("sample")
public class SampleResource {
	@GET
	@Path("hello")
	@Produces("text/plain")
	public String hello() {
		return "hello";
	}

	@GET
	@Path("aaa")
	@Produces("application/json")
	public Data aaa(@QueryParam("name") String name) {
		Data result = new Data();
		result.name = name;
		return result;
	}

	//インナークラスを JAXB で変換させるには static クラスにする必要あり
	@XmlRootElement
	public static class Data {
		public String name;
		public int point;
	}
}
