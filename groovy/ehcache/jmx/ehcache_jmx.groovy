import javax.management.*
import javax.management.remote.*
import com.sun.tools.attach.VirtualMachine

if (args.length < 1) {
	println "groovy ehcache_jmx.groovy <pid> [delay (ms)] [count]"
	return
}

def vm = VirtualMachine.attach(args[0])

def getServiceUrl = {
	vm.agentProperties.getProperty('com.sun.management.jmxremote.localConnectorAddress')
}

if (getServiceUrl() == null) {
	def javaHome = vm.systemProperties.getProperty('java.home')
	vm.loadAgent("${javaHome}/lib/management-agent.jar")
}

def url = getServiceUrl()

vm.detach()

def delay = (args.length < 2)? 0: args[1] as int
def count = (args.length < 3)? 1: args[2] as int

def state = [
	'net.sf.ehcache:type=CacheStatistics,*' : [
		'CacheHits',
		'CacheMisses',
		'InMemoryHits',
		'InMemoryMisses'
	]
]

def con = JMXConnectorFactory.connect(new JMXServiceURL(url))
def server = con.getMBeanServerConnection()

(1..count).each {
	state.each {k, v ->

		server.queryNames(new ObjectName(k), null).each { name ->
			println "*** ${name}"

			v.each {attr ->
				def value = server.getAttribute(name, attr)
				print "$attr=$value "
			}

			println ''
			println ''
		}
	}
	println ''

	sleep delay
}

con.close()
