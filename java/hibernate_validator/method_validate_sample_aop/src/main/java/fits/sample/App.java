package fits.sample;

public class App {
	public static void main(String[] args) throws Exception {
		DataTester tester = new DataTester();

		System.out.println("------ test, 6");
		tester.test("test", 6);

		System.out.println("------ test123, 2");
		tester.test("test123", 2);
	}
}
