package fits.sample;

class App {
	public static void main(String... args) {
		Tuple2<String, Integer> t1 = new Tuple2<>("test", 10);
		Tuple2<String, Integer> t2 = new Tuple2<>("test", 10);
		Tuple2<String, Integer> t3 = new Tuple2<>("test", 20);

		System.out.println("t1 equal t2 = " + t1.equals(t2));
		System.out.println("t1 equal t3 = " + t1.equals(t3));

		System.out.println("t1 = " + t1);
	}
}