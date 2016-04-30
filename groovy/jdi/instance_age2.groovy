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
	def objHeap = vm.saVM().objectHeap
	//def objHeap = vm.saObjectHeap()

	def heapVisitorCls = vm.saVM().class.classLoader.loadClass('sun.jvm.hotspot.oops.HeapVisitor')

	def heapVisitor = [
		prologue: { size -> },
		doObj: { oop -> 
			def clsName = oop.klass.name.asString()
			def age = oop.mark.age()

			println "class=${clsName}, handle=${oop.handle}, age=${age}"

			false
		},
		epilogue: { }
	].asType(heapVisitorCls)

	objHeap.iterate(heapVisitor)

} finally {
	vm.dispose()
}