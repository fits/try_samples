import com.sun.tools.attach.VirtualMachine

import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL

def pid = args[0]
String[] params = args.length > 1 ? ["recording=${args[1]}"] : null

def vm = VirtualMachine.attach(args[0])

try {
	def jmxuri = vm.startLocalManagementAgent()

	JMXConnectorFactory.connect(new JMXServiceURL(jmxuri)).withCloseable {
		def server = it.getMBeanServerConnection()

		def bean = new GroovyMBean(server, 'com.sun.management:type=DiagnosticCommand')

		println bean.jfrCheck(params)
	}

} finally {
	vm.detach()
}
