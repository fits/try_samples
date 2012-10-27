
public class Sample {
	public static void main(String[] args) {
		new Printable(){}.print();
	}

	interface Printable {
		void print() default {
			System.out.println("test");
		}
	}
}
