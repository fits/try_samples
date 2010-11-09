package fits.tools.gae.resteasy;

import org.jboss.resteasy.plugins.providers.RegisterBuiltin;

public aspect RegisterBuiltinAspect {

	void around(): call(void RegisterBuiltin.optionalProvider(..)) || call(void RegisterBuiltin.optionalContextResolver(..)) {

		String dependency = thisJoinPoint.getArgs()[0].toString();

		if (!dependency.contains("jaxb")) {
			proceed();
		}
		else {
			System.out.println("exclude : " + dependency);
		}
	}
	
}
