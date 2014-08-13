/*
 * 指定のホストに SSH で接続してコマンドを実行するスクリプト
 */
@Grab('ch.ethz.ganymed:ganymed-ssh2:262')
import ch.ethz.ssh2.*

def host = args[0]
def user = args[1]
def keyFile = args[2]
def cmd = args[3]

def con = new Connection(host)
con.connect()

if (con.authenticateWithPublicKey(user, new File(keyFile), null)) {

	def session = con.openSession()

	try {
		session.execCommand(cmd)

		println session.stdout.text

		session.close()

	} finally {
		con.close()
	}
}
