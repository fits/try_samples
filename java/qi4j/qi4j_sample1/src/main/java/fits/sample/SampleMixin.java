package fits.sample;

public class SampleMixin implements Sample {
	public String hello(String msg) {
		return "hello, " + msg;
	}
}
