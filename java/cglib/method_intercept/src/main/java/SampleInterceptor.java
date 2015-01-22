
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class SampleInterceptor implements MethodInterceptor {
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		System.out.println("*** intercept : " + args);

		Object res = proxy.invokeSuper(obj, args);
		// Object res = proxy.invoke(obj, args); // NG (loop)

		System.out.println("*** original result : " + res);

		return 2;
	}
}
