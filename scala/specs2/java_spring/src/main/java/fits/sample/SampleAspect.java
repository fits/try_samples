package fits.sample;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;

@Aspect
public class SampleAspect {

	@Around("execution(void SampleWrapper.test())")
	public Object doTest(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println("*** before test");

		Object result = pjp.proceed();

		System.out.println("*** after test");

		return result;
	}
}
