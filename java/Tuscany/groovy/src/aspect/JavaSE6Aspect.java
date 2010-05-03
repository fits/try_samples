package aspect;

import javax.xml.stream.XMLInputFactory;

import org.jboss.aop.Aspect;
import org.jboss.aop.Bind;
import org.jboss.aop.joinpoint.Invocation;
import org.jboss.aop.joinpoint.MethodCalledByMethodInvocation;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;

@Aspect
public class JavaSE6Aspect {

    @Bind(pointcut = "call(* org.apache.tuscany.sca.core.ExtensionPointRegistry->getExtensionPoint(*)) and within(org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntimeBuilder)")
    public Object newInstanceAdvice(MethodCalledByMethodInvocation inv) throws Throwable {

        Object result = null;

        if (inv.getArguments()[0] == XMLInputFactory.class) {
            result = XMLInputFactory.newInstance();
            ((ExtensionPointRegistry)inv.getTargetObject()).addExtensionPoint(result);
        }
        else {
            result = inv.invokeNext();
        }
        
        return result;
    }

    @Bind(pointcut = "call(javax.xml.stream.XMLInputFactory javax.xml.stream.XMLInputFactory->newInstance(java.lang.String, java.lang.ClassLoader))")
    public Object xmlAdvice(Invocation inv) throws Throwable {
        return XMLInputFactory.newInstance();
    }

}
