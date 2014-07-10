
Sample.class.methods.each {
	println "------ ${it} -------"
	it.parameters.each {
		println "name: ${it.name}, type: ${it.type}, ptypename: ${it.parameterizedType.typeName}, annotaions: ${it.annotations}"
	}
}

