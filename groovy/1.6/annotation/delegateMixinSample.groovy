
class SampleFeature {
	def name

	def test(msg) {
		println "${this} : $name - $msg"
	}
}

@Mixin(SampleFeature)
class MixinSample {
	void printName() {
		println name
	}
}

class DelegateSample {

	@Delegate SampleFeature feature = new SampleFeature()

	void printName() {
		println name
	}
}

ms = new MixinSample(name: "Mixinテスト")
ms.printName()
ms.test "call test"

//setName が用意されないため以下は不可
//ds = new DelegateSample(name: "Delegateテスト")
ds = new DelegateSample()
//以下も不可
//ds.name = "Delagateテスト"
ds.feature.name = "Delegateテスト"
ds.printName()
ds.test "call test"


def printMethods = {
	println "--- ${it} methods ---"

	it.metaClass.methods.each {m ->
		println m
	}
}

printMethods DelegateSample

println ""

printMethods MixinSample

