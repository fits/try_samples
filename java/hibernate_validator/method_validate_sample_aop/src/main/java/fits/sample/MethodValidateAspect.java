package fits.sample;

import java.util.*;
import javax.validation.*;

import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;

import org.hibernate.validator.method.*;

@Aspect
public class MethodValidateAspect {

	@Before("call(@ValidMethod * *.*(..))")
	public void checkMethod(JoinPoint jp) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		MethodValidator mvalidator = validator.unwrap(MethodValidator.class);

		MethodSignature msig = (MethodSignature)jp.getSignature();

		Set<MethodConstraintViolation<Object>> violations = mvalidator.validateAllParameters(jp.getTarget(), msig.getMethod(), jp.getArgs());

		for (MethodConstraintViolation<Object> vi : violations) {
			System.out.println("*** invalid : " + vi.getMessage());
		}
	}

}