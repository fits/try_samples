import com.sun.jdi.Bootstrap

def pid = args[0]
def prefix = args[1]

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
	if (vm.canGetInstanceInfo()) {

		def uv = vm.saVM.universe

		def gen = generation(uv.heap())

		def youngGen = gen.young
		def oldGen = gen.old

		println "*** youngGen=${youngGen}, oldGen=${oldGen}"
		println ''

		vm.allClasses().findAll { it.name().startsWith(prefix) }.each { cls ->
			println cls.name()

			cls.instances(0).each { inst ->
				def oop = inst.ref()
				def age = oop.mark.age()

				def inYoung = youngGen.isIn(oop.handle)
				def inOld = oldGen.isIn(oop.handle)

				def identityHash = ''

				try {
					identityHash = Long.toHexString(oop.identityHash())
				} catch (e) {
				}

				println "  hash=${identityHash}, handle=${oop.handle}, age=${age}, inYoung=${inYoung}, inOld=${inOld}"
			}
		}
	}
} finally {
	vm.dispose()
}