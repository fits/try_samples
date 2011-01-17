package fits.sample;

import org.springframework.beans.factory.annotation.Autowired;

public class SampleWrapper {
	@Autowired
	private Sample sample;

	public void test() {
		this.sample.printMessage();
	}
}
