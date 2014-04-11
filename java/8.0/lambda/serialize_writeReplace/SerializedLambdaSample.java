
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.function.Predicate;
import java.io.Serializable;

class SerializedLambdaSample {

	public static void main(String... args) throws Exception {
		SPredicate<Integer> f1 = (x) -> x > 0;

		Method m = f1.getClass().getDeclaredMethod("writeReplace");
		m.setAccessible(true);

		SerializedLambda sl = (SerializedLambda)m.invoke(f1);

		System.out.println(sl);

		System.out.println(sl.getImplClass());
		System.out.println(sl.getImplMethodName());
	}

	public interface SPredicate<T> extends Predicate<T>, Serializable {
	}
}
