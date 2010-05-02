
class Data {
	String name  = "AAA"
}

def methodName = "AAA"

Data.metaClass."nameShouldBe${methodName}" = {
	println delegate.name == methodName
}

def data = new Data()
data.nameShouldBeAAA()

