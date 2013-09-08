package fits.sample;

import javax.inject.Named;
import javax.inject.Inject;

@Named
public class SampleWrapper {
	@Inject
	private Sample sample;

	public void test() {
		this.sample.printMessage();
	}
}
