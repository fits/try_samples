package fits.sample;

public class SampleServiceImpl implements SampleService {
	public String convert(String msg) {
		return msg + "-test-" + this;
	}
}
