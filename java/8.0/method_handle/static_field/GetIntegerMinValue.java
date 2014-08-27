
import java.lang.invoke.MethodHandle;

import static java.lang.invoke.MethodHandles.*;
import static java.lang.invoke.MethodType.*;

public class GetIntegerMinValue {
	public static void main(String... args) throws Throwable {

		MethodHandle mh = publicLookup().findStaticGetter(Integer.class, "MIN_VALUE", int.class);

		System.out.println(mh.invoke());
	}
}