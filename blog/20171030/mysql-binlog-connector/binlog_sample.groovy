@Grab('com.github.shyiko:mysql-binlog-connector-java:0.13.0')
import com.github.shyiko.mysql.binlog.BinaryLogClient

def host = args[0]
def port = args[1] as int
def user = args[2]
def pass = args[3]

def client = new BinaryLogClient(host, port, user, pass)

client.registerEventListener { ev ->
	println ev
}

client.connect()
