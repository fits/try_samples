package fits.sample;

import java.util.function.*;

import org.apache.bcel.Repository;

class LambdaSample {
	public static void main(String... args) throws Exception {
		Predicate<Data> func = (d) -> d.getValue() == 10 || d.getValue() > 100;

		System.out.println(func.getClass());

		printBcelMethods(LambdaSample.class);

		try {
			// could not load dynamic lambda class
			printBcelMethods(func.getClass());
		} catch (Exception e) {
			System.out.println("*** ERROR : " + e.getMessage());
		}
	}

	private static void printBcelMethods(Class<?> cls) throws Exception {
		for(org.apache.bcel.classfile.Method m : Repository.lookupClass(cls).getMethods()) {
			System.out.println("-----");
			System.out.println(m.getName());
			System.out.println(m.getNameIndex());
			System.out.println(m.getSignature());
			System.out.println(m.getCode());
		}
	}

	static class Data {
		private int value;

		public Data(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
}
