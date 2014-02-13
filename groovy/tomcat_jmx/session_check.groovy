import javax.management.*
import javax.management.remote.*

def url = "service:jmx:rmi:///jndi/rmi://localhost:8008/jmxrmi"

def con = JMXConnectorFactory.connect(new JMXServiceURL(url))
def server = con.getMBeanServerConnection()

def obj = new ObjectName('Catalina:type=Manager,context=/sample,host=localhost')

def sessionIds = server.invoke(obj, 'listSessionIds', null, null)

sessionIds.split(' ').each { sid ->
	println "----- ${sid} -----"

	// セッションの user_id 属性値を出力
	println server.invoke(obj, 'getSessionAttribute', [sid, 'user_id'] as String[], [String.class.name, String.class.name] as String[])
}

con.close()
