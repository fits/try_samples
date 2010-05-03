import org.jboss.aop.joinpoint.Invocation;
import org.jboss.aop.advice.Interceptor;

public class TestCallerInterceptor1 implements Interceptor {
	
	public String getName() {
		return "TestCallerInterceptor1";
	}

	public Object invoke(Invocation invocation) throws Throwable {
		this.printOut("invocation class:" + invocation.getClass().getSuperclass());
		this.printOut("target:" + invocation.getTargetObject());
		this.printOut("start method");

		Object result = invocation.invokeNext();

		this.printOut("value : " + result);
		this.printOut("end method");

		return result;
	}

	private void printOut(String msg) {
		System.out.println(getClass().toString() + "-" + msg);
	}

}