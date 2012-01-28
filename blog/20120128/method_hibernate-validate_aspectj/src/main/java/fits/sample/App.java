package fits.sample;

public class App {
	public static void main(String[] args) throws Exception {
		DataTester tester = new DataTester();

		System.out.println("------ check : test, 6");
		tester.test("test", 6);

		System.out.println("------ check : test123, 2");
		tester.test("test123", 2);

		System.out.println("------ check : null, 3");
		tester.test(null, 3);

		System.out.println("------ check : テスト, 10");
		tester.test("テスト", 10);
	}
}
