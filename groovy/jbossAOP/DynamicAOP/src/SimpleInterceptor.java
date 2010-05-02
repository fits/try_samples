import org.jboss.aop.joinpoint.Invocation;
import org.jboss.aop.advice.Interceptor;

public class SimpleInterceptor implements Interceptor {
	
	public String getName() {
		return "SimpleInterceptor";
	}

	public Object invoke(Invocation invocation) throws Throwable {
		this.printOut("simple class:" + invocation.getClass() + ", target:" + invocation.getTargetObject());

		return invocation.invokeNext();
	}

	private void printOut(String msg) {
		System.out.println(getClass().toString() + "-" + msg);
	}

}