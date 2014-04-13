
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.function.Predicate;
import java.io.Serializable;

class Sample1 {
	public static void main(String... args) throws Exception {
		int a = 3;

		SPredicate<Integer> f = (x) -> x % a == 0;

		Method m = f.getClass().getDeclaredMethod("writeReplace");
		m.setAccessible(true);

		SerializedLambda sl = (SerializedLambda)m.invoke(f);

		System.out.println(sl);

		System.out.println("-----");

		System.out.println(sl.getImplClass());
		System.out.println(sl.getImplMethodName());

		for (int i = 0; i < sl.getCapturedArgCount(); i++) {
			System.out.println("arg: " + sl.getCapturedArg(i));
		}
	}

	public interface SPredicate<T> extends Predicate<T>, Serializable {
	}
}
