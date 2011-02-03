package fits.sample;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;

@Aspect
public class SampleAspect {

	@Around("execution(void DbOperations+.put(..))")
	public Object aroundPut(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println("*** before put");

		Object result = pjp.proceed();

		System.out.println("*** after put");

		return result;
	}
}
