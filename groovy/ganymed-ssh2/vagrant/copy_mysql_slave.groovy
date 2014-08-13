@Grab('ch.ethz.ganymed:ganymed-ssh2:262')
@Grab('commons-cli:commons-cli:1.2')
import ch.ethz.ssh2.Connection
import org.apache.commons.cli.Options
import org.apache.commons.cli.PosixParser
import org.apache.commons.cli.HelpFormatter

def parseArgs = { cmdArgs ->
	def opt = new Options()
	opt.addOption('s', 'sourcePort', true, 'slave source port')
	opt.addOption('t', 'targetHost', true, 'slave target host')
	opt.addOption('e', 'encode', true, 'encode')

	opt.addOption('h', 'help', false, 'help')

	def res = new PosixParser().parse(opt, cmdArgs)

	if (res.hasOption('h') || !res.hasOption('s') || !res.hasOption('t')) {
		new HelpFormatter().printHelp('copy_mysql_slave', opt, true)
		System.exit(0)
	}
	res
}

def getArg = { cmdArg, opt, defaultValue = '' ->
	cmdArg.hasOption(opt)? cmdArg.getOptionValue(opt): defaultValue
}

def execCmd = { connection, enc, command ->
	def session = connection.openSession()

	session.execCommand(command)

	def res = session.stdout.getText(enc)

	session.close()

	if (session.exitStatus != 0) {
		throw new Exception(session.stderr.getText(enc))
	}
	res
}

def samePos = { status -> 
	def items = status.split('\n')
	items.first().split(':').last() == items.last().split(':').last()
}

def cmdArg = getArg.curry(parseArgs(args))

def con = new Connection('127.0.0.1', cmdArg('s') as int)
con.connect()

if (con.authenticateWithPublicKey('vagrant', new File('insecure_private_key'), null)) {

	def ssh = execCmd.curry(con, cmdArg('e', 'UTF-8'))

	println ssh('mysql -u root -e "stop slave"')

	if (samePos(ssh('mysql -u root -e "show slave status \\G" | grep Master_Log_Pos'))) {
		println ssh("sudo rsync -av -e \"sshpass -p vagrant ssh -o StrictHostKeyChecking=no\" /var/lib/mysql/ root@${cmdArg('t')}:/var/lib/mysql/")

	}

	println ssh('mysql -u root -e "start slave"')
}

con.close()
