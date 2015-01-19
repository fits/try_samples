
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public class MethodUtil {
	public static MethodHandle partial(Method m, Object... args) throws IllegalAccessException {
		MethodHandle mh = MethodHandles.publicLookup().unreflect(m);
		return MethodHandles.insertArguments(mh, 0, args);
	}
}