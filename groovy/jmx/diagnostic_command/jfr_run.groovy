import com.sun.tools.attach.VirtualMachine

import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL

def pid = args[0]
def duration = args[1]
def fileName = args[2]

def vm = VirtualMachine.attach(args[0])

try {
	def jmxuri = vm.startLocalManagementAgent()

	JMXConnectorFactory.connect(new JMXServiceURL(jmxuri)).withCloseable {
		def server = it.getMBeanServerConnection()

		def bean = new GroovyMBean(server, 'com.sun.management:type=DiagnosticCommand')

		println bean.vmUnlockCommercialFeatures()

		println bean.jfrStart([
			"duration=${duration}",
			"filename=${fileName}",
			'delay=10s'
		] as String[])
	}

} finally {
	vm.detach()
}
