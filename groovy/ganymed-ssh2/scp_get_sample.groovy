/*
 * 指定のホストから SCP でファイルをコピーするスクリプト
 */
@Grab('ch.ethz.ganymed:ganymed-ssh2:build210')
import ch.ethz.ssh2.*

def host = args[0]
def user = args[1]
def keyFile = args[2]
def remoteLogDir = args[3]
def destDir = args[4]

def con = new Connection(host)
con.connect()

if (con.authenticateWithPublicKey(user, new File(keyFile), null)) {
	println "connected : $host"

	try {
		def session = con.openSession()
		/* SCPClient にはディレクトリ内の全ファイルを
		 * 取得するような API が無いため、
		 * ls で取得して 1ファイルずつコピーする
		 */
		session.execCommand "ls ${remoteLogDir}"

		def list = session.stdout.text

		def scp = con.createSCPClient()

		def files = list.split().collect {
			"${remoteLogDir}/$it"
		} as String[]

		scp.get(files, destDir)

		println "copied    : $host"

		session.close()

	} finally {
		con.close()
	}
}
