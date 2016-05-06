import com.sun.jdi.Bootstrap

def pid = args[0]
def prefix = (args.length > 1)? args[1]: ''

def manager = Bootstrap.virtualMachineManager()

def connector = manager.attachingConnectors().find {
	it.name() == 'sun.jvm.hotspot.jdi.SAPIDAttachingConnector'
}

def params = connector.defaultArguments()
params.get('pid').setValue(pid)

def vm = connector.attach(params)

generation = { heap ->
	def hasYoungGen = heap.metaClass.getMetaMethod('youngGen') != null

	[
		young: hasYoungGen? heap.youngGen(): heap.getGen(0),
		old: hasYoungGen? heap.oldGen(): heap.getGen(1)
	]
}

try {
	def uv = vm.saVM.universe

	def gen = generation(uv.heap())

	def youngGen = gen.young
	def oldGen = gen.old

	println "*** youngGen=${youngGen}, oldGen=${oldGen}"
	println ''

	def objHeap = vm.saObjectHeap()
	//def objHeap = vm.saVM.objectHeap

	def heapVisitorCls = uv.class.classLoader.loadClass('sun.jvm.hotspot.oops.HeapVisitor')

	def heapVisitor = [
		prologue: { size -> },
		epilogue: {},
		doObj: { oop ->
			def clsName = oop.klass.name.asString()

			if (clsName.startsWith(prefix)) {
				def age = oop.mark.age()

				def inYoung = youngGen.isIn(oop.handle)
				def inOld = oldGen.isIn(oop.handle)

				def identityHash = ''

				try {
					identityHash = Long.toHexString(oop.identityHash())
				} catch (e) {
				}

				println "class=${clsName}, hash=${identityHash}, handle=${oop.handle}, age=${age}, inYoung=${inYoung}, inOld=${inOld}"
			}

			false
		}
	].asType(heapVisitorCls)

	objHeap.iterate(heapVisitor)

} finally {
	vm.dispose()
}