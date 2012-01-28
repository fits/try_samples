package fits.sample;

import javax.validation.constraints.*;

public class DataTester {
	@ValidMethod
	public void test(
		@NotNull @Size(max = 5) String name,
		@Min(3) int point) {

		System.out.printf("%s, %d\n", name, point);
	}

	public void testNoCheck(
		@NotNull @Size(max = 5) String name,
		@Min(3) int point) {

		System.out.printf("%s, %d\n", name, point);
	}

}
