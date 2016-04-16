
import com.sun.tools.attach.VirtualMachine

import java.lang.management.ThreadMXBean
import java.lang.management.ManagementFactory

import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL

def vm = VirtualMachine.attach(args[0])

try {
	def jmxuri = vm.startLocalManagementAgent()

	JMXConnectorFactory.connect(new JMXServiceURL(jmxuri)).withCloseable {
		def server = it.getMBeanServerConnection()

		def bean = ManagementFactory.newPlatformMXBeanProxy(server, 'java.lang:type=Threading', ThreadMXBean)

		bean.dumpAllThreads(false, false).each {
			println it
		}
	}

} finally {
	vm.detach()
}
