
package sample.client;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import sample.service.TestService;

public class TestClientActivator implements BundleActivator {

	public void start(BundleContext context) {
		ServiceReference sr = context.getServiceReference(TestService.class.getName());

		if (sr != null) {
			TestService service = (TestService)context.getService(sr);

			if (service != null) {
				System.out.println("test service result : " + service.test("ABC"));

				context.ungetService(sr);
			}
		}
	}

	public void stop(BundleContext context) {
	}
}
