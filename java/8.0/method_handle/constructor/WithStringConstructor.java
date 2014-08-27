
import java.lang.invoke.MethodHandle;

import static java.lang.invoke.MethodHandles.*;
import static java.lang.invoke.MethodType.*;

public class WithStringConstructor {
	public static void main(String... args) throws Throwable {
		Class cls = Class.forName(args[0]);

		MethodHandle mh = publicLookup().findConstructor(cls, methodType(void.class, String.class));

		System.out.println(mh.invoke(args[1]));
	}
}