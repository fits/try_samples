@Grab('ch.ethz.ganymed:ganymed-ssh2:262')
import ch.ethz.ssh2.Connection
import groovyx.gpars.GParsPool

def cmdList = [
	'sudo yum update -y',
	'sudo yum install -y http://dev.mysql.com/get/mysql-community-release-el7-5.noarch.rpm',
	'sudo yum install -y mysql-community-server',
	'sudo firewall-cmd --add-service=mysql --permanent',
	'sudo firewall-cmd --reload'
]

def con = new Connection('127.0.0.1', args[0] as int)
con.connect()

if (con.authenticateWithPublicKey('vagrant', new File('insecure_private_key'), null)) {
	def session = con.openSession()

	session.execCommand(cmdList.join(';'))

	def streams = [session.stdout, session.stderr]

	GParsPool.withPool(streams.size()) {
		streams.eachParallel { st ->
			st.eachLine { println it }
		}
	}

	session.close()
}

con.close()
