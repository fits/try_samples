
import com.sun.tools.attach.VirtualMachine

import java.lang.management.MemoryPoolMXBean
import java.lang.management.ManagementFactory

import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL

def pid = args[0]
def name = args[1]

def vm = VirtualMachine.attach(pid)

try {
	def jmxuri = vm.startLocalManagementAgent()

	JMXConnectorFactory.connect(new JMXServiceURL(jmxuri)).withCloseable {
		def server = it.getMBeanServerConnection()

		def bean = ManagementFactory.newPlatformMXBeanProxy(server, "${ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE},name=${name}", MemoryPoolMXBean)

		println "----- ${bean.name} -----"

//		def pu = bean.peakUsage
		def pu = bean.usage

		def usage = pu.used / pu.max

		println "init = ${pu.init}, used = ${pu.used}, committed = ${pu.committed}, max = ${pu.max}, usage = ${usage.setScale(4, BigDecimal.ROUND_UP)}"
	}

} finally {
	vm.detach()
}
