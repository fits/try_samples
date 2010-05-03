
package simple;

import org.apache.tuscany.sca.extension.helper.InvokerFactory;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;

public class SimpleInvokerFactory implements InvokerFactory {

    private SimpleImplementation impl;

    public SimpleInvokerFactory(SimpleImplementation impl) {
        this.impl = impl;
    }

    public Invoker createInvoker(Operation operation) {
        System.out.println("call createInvoker : " + operation + ", name: " + operation.getName() + ", unresolved:" + operation.isUnresolved() + ", inputType:" + operation.getInputType() + ", outputType:" + operation.getOutputType() + ", interface:" + operation.getInterface() + ", non blocking:" + operation.isNonBlocking() + ", databinding:" + operation.getDataBinding() + ", wrapper:" + operation.getWrapper());
        
        return new SimpleInvoker(impl);
    }

}
