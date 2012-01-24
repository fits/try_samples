@Grab("org.hibernate:hibernate-validator:4.2.0.Final")

import javax.validation.*
import javax.validation.constraints.*

class Data {
	public void test(
		@NotNull @Size(max = 5) String name,
		@Min(3L) int point) {

		println "${name}, ${point}"
	}
}

def data = new Data()

def factory = Validation.buildDefaultValidatorFactory()
def validator = factory.validator

def mvalidator = validator.unwrap(org.hibernate.validator.method.MethodValidator.class)

java.lang.reflect.Method mt = Data.class.getMethod("test", String.class, int.class)

mvalidator.validateAllParameters(data, mt, ["test", 6] as Object[]).each {
	println it.message
}
println "------"

mvalidator.validateAllParameters(data, mt, ["test12", 1] as Object[]).each {
	println it.message
}