package fits.sample;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class DataTester {
	@ValidMethod
	public void test(
		@NotNull @Size(max = 5) String name,
		@Min(3) int point) {

		System.out.printf("%s, %d\n", name, point);
	}
}
