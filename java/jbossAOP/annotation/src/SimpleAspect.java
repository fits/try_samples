
import org.jboss.aop.Aspect;
import org.jboss.aop.Bind;
import org.jboss.aop.joinpoint.MethodInvocation;

@Aspect
public class SimpleAspect {

    @Bind(pointcut = "execution(* *->addData(..))")
    public Object addDataAdvice(MethodInvocation invocation) throws Throwable {
        this.printOut("simple class:" + invocation.getClass() + ", target:" + invocation.getTargetObject());

        return invocation.invokeNext();
    }

    private void printOut(String msg) {
        System.out.println(getClass().toString() + "-" + msg);
    }

}