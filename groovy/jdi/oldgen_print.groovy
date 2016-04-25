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
	def uv = vm.saVM.universe

	def oldGen = uv.heap().oldGen()

	println "used: ${oldGen.used()}, capacity: ${oldGen.capacity()}"

	oldGen.objectSpace().liveRegions.each {
		println it.byteSize()
	}

} finally {
	vm.dispose()
}