package fits.sample.service;

public class SampleServiceImpl implements SampleService {

	@Override
	public String message(String data) {
		return "hello " + data;
	}

}
