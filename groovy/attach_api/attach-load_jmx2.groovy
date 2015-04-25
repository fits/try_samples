import com.sun.tools.attach.VirtualMachine
import sun.management.ConnectorAddressLink

def vm = VirtualMachine.attach(args[0])

def getConnectorAddress = {
	ConnectorAddressLink.importFrom(args[0] as int)
}

if (getConnectorAddress() == null) {
	def javaHome = vm.systemProperties.getProperty('java.home')

	vm.loadAgent("${javaHome}/lib/management-agent.jar")

	println '*** load agent'
}

println getConnectorAddress()

vm.detach()
