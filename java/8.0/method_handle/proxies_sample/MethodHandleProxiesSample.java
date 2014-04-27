
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandle;
import java.util.function.Function;

import static java.lang.invoke.MethodHandles.*;
import static java.lang.invoke.MethodType.*;

public class MethodHandleProxiesSample {
	public static void main(String... args) throws Throwable {

		MethodHandle mh = publicLookup().findVirtual(String.class, "replaceAll", methodType(String.class, String.class, String.class));

		MethodHandle mh2 = mh.bindTo("a,b,c,d,e,f").bindTo(",");

		Function<String, String> f = MethodHandleProxies.asInterfaceInstance(Function.class, mh2);

		System.out.println(f.apply("-"));
	}
}