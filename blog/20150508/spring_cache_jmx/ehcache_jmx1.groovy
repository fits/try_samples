import javax.management.ObjectName
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL

import com.sun.tools.attach.VirtualMachine

def getServiceUrl = {
	it.agentProperties.getProperty('com.sun.management.jmxremote.localConnectorAddress')
}

def pid = args[0]
def vm = VirtualMachine.attach(pid)

def url = getServiceUrl(vm)

if (url == null) {
	def javaHome = vm.systemProperties.getProperty('java.home')
	vm.loadAgent("${javaHome}/lib/management-agent.jar")

	url = getServiceUrl(vm)
}

vm.detach()

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
