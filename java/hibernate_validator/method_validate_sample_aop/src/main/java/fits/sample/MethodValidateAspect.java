package fits.sample;

import java.util.*;
import javax.validation.*;

import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;

import org.hibernate.validator.method.*;

@Aspect
public class MethodValidateAspect {

	@Around("execution(void DataTester.test(..))")
	public void checkMethod(ProceedingJoinPoint pjp) throws Throwable {
		MethodSignature msig = (MethodSignature)pjp.getSignature();

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		MethodValidator mvalidator = validator.unwrap(MethodValidator.class);

		Set<MethodConstraintViolation<Object>> violations = mvalidator.validateAllParameters(pjp.getThis(), msig.getMethod(), pjp.getArgs());

		for (MethodConstraintViolation<Object> vi : violations) {
			System.out.println("*** invalid : " + vi.getMessage());
		}

		pjp.proceed();
	}

}