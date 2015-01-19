
import java.lang.invoke.MethodHandle;

public class Sample {
	public static void main(String... args) throws Throwable {

		MethodHandle mh = MethodUtil.partial(
			String.class.getMethod("lastIndexOf", String.class, int.class),
			"test data",
			"da"
		);

		System.out.println(mh.invoke(0));
		System.out.println(mh.invoke(4));
		System.out.println(mh.invoke(6));
		System.out.println(mh.invoke(10));
	}
}