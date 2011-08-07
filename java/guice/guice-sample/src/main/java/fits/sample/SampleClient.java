package fits.sample;

import javax.inject.Inject;

public class SampleClient {
	@Inject
	private SampleService service;

	public void print(String msg) {
		System.out.println(service.convert(msg));
	}
}
