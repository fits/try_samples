
package simple;

import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.extension.helper.ImplementationActivator;
import org.apache.tuscany.sca.extension.helper.InvokerFactory;
import org.apache.tuscany.sca.extension.helper.utils.PropertyValueObjectFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

public class SimpleImplementationActivator implements ImplementationActivator<SimpleImplementation> {

    public Class<SimpleImplementation> getImplementationClass() {
        System.out.println("call getImplementationClass");

        return SimpleImplementation.class;
    }

    public InvokerFactory createInvokerFactory(RuntimeComponent rc, ComponentType ct, SimpleImplementation implementation) {

        System.out.println("call createInvokerFactory");

        return new SimpleInvokerFactory(implementation);
    }

}
