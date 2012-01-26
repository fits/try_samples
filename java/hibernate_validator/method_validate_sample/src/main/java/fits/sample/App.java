package fits.sample;

import java.lang.reflect.Method;
import java.util.*;
import javax.validation.*;
import org.hibernate.validator.method.*;

public class App {
	public static void main(String[] args) throws Exception {
		DataTester tester = new DataTester();

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		MethodValidator mvalidator = validator.unwrap(MethodValidator.class);

		Method mt = DataTester.class.getMethod("test", String.class, int.class);

		Set<MethodConstraintViolation<DataTester>> res = mvalidator.validateAllParameters(tester, mt, new Object[]{"test", 6});

		System.out.println("------ test, 6");
		for (MethodConstraintViolation<DataTester> vl : res) {
			System.out.println(vl.getMessage());
		}

		res = mvalidator.validateAllParameters(tester, mt, new Object[]{"test123", 2});

		System.out.println("------ test123, 2");
		for (MethodConstraintViolation<DataTester> vl : res) {
			System.out.println(vl.getMessage());
		}

	}
}
