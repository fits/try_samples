
package sample;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import sample.service.TestService;

public class TestActivator implements BundleActivator {

	public void start(BundleContext context) {
		context.registerService(TestService.class.getName(), new TestServiceImpl(), null);
	}

	public void stop(BundleContext context) {
	}

	private class TestServiceImpl implements TestService {
		public String test(String msg) {
			return msg + "_test";
		}
	}
}
