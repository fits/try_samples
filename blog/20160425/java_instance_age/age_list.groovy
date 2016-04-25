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

try {
	if (vm.canGetInstanceInfo()) {

		vm.allClasses().findAll { it.name().startsWith(prefix) }.each { cls ->
			println cls.name()

			cls.instances(0).each { inst ->
				def oop = inst.ref()
				def age = oop.mark.age()

				println "  handle=${oop.handle}, age=${age}"
			}
		}
	}
} finally {
	vm.dispose()
}