
import java.util.stream.Stream;

public class ClassAnyMatch {
	public static void main(String... args) {
		boolean r1 = Stream.of(new A(), new A(), new B()).anyMatch(A.class::isInstance);

		System.out.println(r1);

		boolean r2 = Stream.of(new A(), new A(), new B()).anyMatch(B.class::isInstance);

		System.out.println(r2);

		boolean r3 = Stream.of(new A(), new A(), new B()).anyMatch(C.class::isInstance);

		System.out.println(r3);
	}

	public static interface Base {
	}

	public static class A implements Base {
	}

	public static class B implements Base {
	}

	public static class C implements Base {
	}
}

