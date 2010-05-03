
package sample;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class SampleActivator implements BundleActivator {

	public void start(BundleContext context) {
		System.out.println("start : " + getClass());
	}

	public void stop(BundleContext context) {
		System.out.println("stop : " + getClass());
	}
}
