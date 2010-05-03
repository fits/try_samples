
package simple;

import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

public class SimpleInvoker implements Invoker {

    private SimpleImplementation impl;

    public SimpleInvoker(SimpleImplementation impl) {
        this.impl = impl;
    }

    public Message invoke(Message msg) {

        Object[] params = (Object[])msg.getBody();

        System.out.printf("body: %s, operation: %s, inputtype: %s", params[0], msg.getOperation(), msg.getOperation().getInputType());
        
        msg.setBody(params[0] + "_simple");
        
        return msg;
    }

}
