package fits.sample;

import org.aspectj.lang.annotation.*;
import org.aspectj.lang.*;

@Aspect
public class BeanUtilsAspect {
	private final static String INVALID_PROPERTY = "classLoader";

	@Around("execution(String org.apache.commons.beanutils.expression.Resolver+.next(String)) && args(expression)")
	public String aroundNext(ProceedingJoinPoint pjp, String expression) {

		String res = (String)pjp.proceed();

		if (INVALID_PROPERTY.equalsIgnoreCase(res)) {
			throw new IllegalArgumentException(expression + " contains invalid property '" + INVALID_PROPERTY + "'");
		}

		return res;
	}
}
