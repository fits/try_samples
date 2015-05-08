import javax.management.ObjectName
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL

import com.sun.tools.attach.VirtualMachine
import sun.management.ConnectorAddressLink

def getServiceUrl = {
	ConnectorAddressLink.importFrom(it as int)
}

def pid = args[0]
def url = getServiceUrl(pid)

if (url == null) {
	def vm = VirtualMachine.attach(pid)

	def javaHome = vm.systemProperties.getProperty('java.home')
	vm.loadAgent("${javaHome}/lib/management-agent.jar")

	vm.detach()

	url = getServiceUrl(pid)
}

def con = JMXConnectorFactory.connect(new JMXServiceURL(url))
def server = con.getMBeanServerConnection()

def cacheName = new ObjectName('net.sf.ehcache:type=CacheStatistics,*')

server.queryNames(cacheName, null).each { name ->
	println "# ${name}"

	def res = [
		'CacheHits',
		'CacheMisses',
		'InMemoryHits',
		'InMemoryMisses'
	].collectEntries { attr ->
		[attr, server.getAttribute(name, attr)]
	}

	println res
}

con.close()
