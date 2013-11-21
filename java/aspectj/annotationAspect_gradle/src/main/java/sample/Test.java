package sample;

public class Test {

	public static void main(String[] args) {

		Data data = createData();

		System.out.printf("name: %s", data.getName());
	}

	private static Data createData() {
		Data result = new Data();
		result.setName("bbb");
		return result;
	}

}
