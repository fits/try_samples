@Grab('org.functionaljava:functionaljava:4.4')
import fj.data.Validation

def printlnV = { v ->
	try {
		println "success: ${v.success()}"
	} catch (Error e) {
		println "success error: ${e}"
	}

	try {
		println "fail: ${v.fail()}"
	} catch (Error e) {
		println "fail error: ${e}"
	}

	println ''
}

def v1 = Validation.success("sample")

printlnV v1

def v2 = Validation.fail(new Exception("test"))

printlnV v2
