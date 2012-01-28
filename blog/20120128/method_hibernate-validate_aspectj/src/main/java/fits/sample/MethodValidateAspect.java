package fits.sample;

import java.util.Set;
import javax.validation.Validator;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;

import org.hibernate.validator.method.MethodValidator;
import org.hibernate.validator.method.MethodConstraintViolation;

@Aspect
public class MethodValidateAspect {

	@Before("execution(@ValidMethod * *.*(..))")
	public void checkMethod(JoinPoint jp) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		// MethodValidor の取得
		MethodValidator mvalidator = validator.unwrap(MethodValidator.class);

		//java.lang.reflect.Method を取得するため MethodSignature にキャスト
		MethodSignature msig = (MethodSignature)jp.getSignature();

		//メソッド引数のチェック
		Set<MethodConstraintViolation<Object>> violations = mvalidator.validateAllParameters(jp.getThis(), msig.getMethod(), jp.getArgs());

		//結果出力
		for (MethodConstraintViolation<Object> vi : violations) {
			System.out.printf("*** invalid arg : %s, %s\n", vi.getParameterName(), vi.getMessage());
		}
	}

}