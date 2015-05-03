import javax.management.*
import javax.management.remote.*
import com.sun.tools.attach.VirtualMachine
import sun.management.ConnectorAddressLink

if (args.length < 1) {
	println "groovy ehcache_jmx2.groovy <pid> [delay (ms)] [count]"
	return
}

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
