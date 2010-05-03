package aspect;

import java.util.*;
import javax.xml.stream.XMLInputFactory;

import org.jboss.aop.Aspect;
import org.jboss.aop.Bind;
import org.jboss.aop.joinpoint.*;

import org.apache.tuscany.sca.core.*;

@Aspect
public class JavaSE6Aspect {

    @Bind(pointcut = "call(* org.apache.tuscany.sca.core.ExtensionPointRegistry->getExtensionPoint(*)) and within(org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntimeBuilder)")
    public Object newInstanceAdvice(MethodCalledByMethodInvocation inv) throws Throwable {

        System.out.printf("------ call getServiceClassNames : %s\n", inv.getArguments()[0]);

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
