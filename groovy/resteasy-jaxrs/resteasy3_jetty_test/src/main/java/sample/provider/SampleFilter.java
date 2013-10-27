package sample.provider;

import java.lang.reflect.Method;

import javax.ws.rs.ext.Provider;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.jboss.resteasy.core.ResourceMethodInvoker;

import sample.annotation.Sample;

@Provider
public class SampleFilter implements ContainerRequestFilter {
	@Override
	public void filter(ContainerRequestContext context) {
		System.out.println("*** filter " + context);

		ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker)context.getProperty(ResourceMethodInvoker.class.getName());

		Method method = methodInvoker.getMethod();

		if (method.getAnnotation(Sample.class) != null) {
			System.out.println("*** sample annotation");
		}
		else {
			System.out.println("*** no sample annotation");
		}
	}
}
