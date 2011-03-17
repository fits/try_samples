package fits.sample;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class SampleWrapper {
	@Autowired
	private Sample sample;

	public void test() {
		this.sample.printMessage();
	}
}
