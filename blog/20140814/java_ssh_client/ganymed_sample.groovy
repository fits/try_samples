@Grab('ch.ethz.ganymed:ganymed-ssh2:262')
import ch.ethz.ssh2.Connection

def con = new Connection('127.0.0.1', 2222)
con.connect()

if (con.authenticateWithPublicKey('vagrant', new File('insecure_private_key'), null)) {
	def session = con.openSession()

	session.execCommand('ls -al')

	println session.stdout.text

	session.close()
}

con.close()
