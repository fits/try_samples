@Grab("org.hibernate:hibernate-validator:4.2.0.Final")

import javax.validation.*
import javax.validation.constraints.*

class Data {
	@NotNull
	@Size(max = 5)
	public String name
	@Min(3L)
	public int point
}

def factory = Validation.buildDefaultValidatorFactory()
def validator = factory.validator

validator.validate(new Data(name: "test", point: 10)).each {
	println it.message
}

println "----------"
validator.validate(new Data(name: "test12", point: 10)).each {
	println it.message
}

println "----------"
validator.validate(new Data(point: 1)).each {
	println it.message
}


