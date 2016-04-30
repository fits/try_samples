import com.sun.jdi.Bootstrap

def pid = args[0]

def manager = Bootstrap.virtualMachineManager()

def connector = manager.attachingConnectors().find {
	it.name() == 'sun.jvm.hotspot.jdi.SAPIDAttachingConnector'
}

def params = connector.defaultArguments()
params.get('pid').setValue(pid)

def vm = connector.attach(params)

try {
	def universe = vm.saVM().universe

	def oldGen = universe.heap().oldGen()
	def youngGen = universe.heap().youngGen()

	def objHeap = vm.saVM().objectHeap
	//def objHeap = vm.saObjectHeap()

	def heapVisitorCls = vm.saVM().class.classLoader.loadClass('sun.jvm.hotspot.oops.HeapVisitor')

	def hvisitor = [
		prologue: { size -> },
		doObj: { oop -> 
			def clsName = oop.klass.name.asString()
			def age = oop.mark.age()

			def idHash = ''
			def slowIdHash = ''

			try {
				idHash = Long.toHexString(oop.identityHash())
				slowIdHash = Long.toHexString(oop.slowIdentityHash())
			} catch (e) {
				//e.printStackTrace()
			}

			def inYoung = youngGen.isIn(oop.handle)
			def inOld = oldGen.isIn(oop.handle)

			println "class=${clsName}, hash=${idHash}, slowHash=${slowIdHash}, handle=${oop.handle}, age=${age}, inYoung=${inYoung}, inOld=${inOld}"

			false
		},
		epilogue: { }
	].asType(heapVisitorCls)

	objHeap.iterate(hvisitor)

} finally {
	vm.dispose()
}