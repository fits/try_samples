@Grab('ch.ethz.ganymed:ganymed-ssh2:262')
import ch.ethz.ssh2.Connection

def configFile = (args.length > 0)? args[0]: 'config.properties'

def setting = new Properties()
setting.load(new File(configFile).newInputStream())

def con = new Connection(setting.host, setting.port as int)
con.connect()

try {
	if (con.authenticateWithPublicKey(setting.user, new File(setting.key_file), setting.password)) {

		def fd = con.createLocalPortForwarder(
			setting.forward_local_port as int,
			setting.forward_host,
			setting.forward_port as int
		)

		println 'forwading...'

		System.in.read()

		fd.close()
	}
} finally {
	con.close()
}
