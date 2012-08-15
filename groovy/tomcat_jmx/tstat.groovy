import javax.management.*
import javax.management.remote.*

if (args.length < 1) {
	println "groovy tstat.groovy <host:port> [delay (ms)] [count]"
	return
}

def url = "service:jmx:rmi:///jndi/rmi://${args[0]}/jmxrmi"
def delay = (args.length < 2)? 0: args[1] as int
def count = (args.length < 3)? 1: args[2] as int

def state = [
	'Catalina:type=ThreadPool,name="http-bio-8080"' : [
		'connectionCount',
		'currentThreadsBusy',
		'currentThreadCount'
	],
	'Catalina:type=DataSource,class=javax.sql.DataSource,name="jdbc/testdb"' : [
		'numActive',
		'numIdle'
	]
]

def con = JMXConnectorFactory.connect(new JMXServiceURL(url))
def server = con.getMBeanServerConnection()

(1..count).each {
	state.each {k, v ->
		v.each {attr ->
			def value = server.getAttribute(new ObjectName(k), attr)
			print "$attr=$value "
		}
	}
	println ""

	sleep delay
}

con.close()
