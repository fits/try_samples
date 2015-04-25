import com.sun.tools.attach.VirtualMachine

def vm = VirtualMachine.attach(args[0])

def getConnectorAddress = {
	vm.agentProperties.getProperty('com.sun.management.jmxremote.localConnectorAddress')
}

if (getConnectorAddress() == null) {
	def javaHome = vm.systemProperties.getProperty('java.home')

	vm.loadAgent("${javaHome}/lib/management-agent.jar")

	print getConnectorAddress()
}
