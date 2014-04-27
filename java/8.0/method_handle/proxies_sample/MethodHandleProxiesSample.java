
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandle;
import java.util.function.Function;

import static java.lang.invoke.MethodHandles.*;
import static java.lang.invoke.MethodType.*;

public class MethodHandleProxiesSample {
	public static void main(String... args) throws Throwable {

		MethodHandle mh = publicLookup().findVirtual(String.class, "replaceAll", methodType(String.class, String.class, String.class));

		MethodHandle mh2 = mh.bindTo("a,b,c,d,e,f").bindTo(",");

		@SuppressWarnings("unchecked")
		Function<String, String> f1 = MethodHandleProxies.asInterfaceInstance(Function.class, mh2);

		System.out.println(f1.apply("-"));

		System.out.println("-----");

		Function<String, String> f2 = s -> "a,b,c,d,e,f".replaceAll(",", s);

		System.out.println(f2.apply("-"));
	}
}