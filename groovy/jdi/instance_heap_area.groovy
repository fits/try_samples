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

/*
def mark = {
	it.hasDisplacedMarkHelper()? it.displacedMarkHelper(): it

}
*/

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

		def oldGen = gen.old
		def youngGen = gen.young

		vm.allClasses().findAll { it.name().startsWith(prefix) }.each { cls ->
			println cls.name()

			cls.instances(0).each { inst ->
				def oop = inst.ref()
				def mark = oop.getMark()

				def hash1 = ''
				def hash2 = ''

				try {
					hash1 = Long.toHexString(oop.slowIdentityHash())
					hash2 = Long.toHexString(oop.identityHash())
				} catch (e) {
				}

				def inYoung = youngGen.isIn(oop.handle)
				def inOld = oldGen.isIn(oop.handle)

				println "  hash1=${hash1}, hash2=${hash2}, handle=${oop.handle}, age=${mark.age()}, inYoung=${inYoung}, inOld=${inOld}"
			}
		}
	}
} finally {
	vm.dispose()
}