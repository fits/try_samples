@GrabConfig(systemClassLoader = true)
@Grab('mysql:mysql-connector-java:5.1.31')
@Grab('org.postgresql:postgresql:9.3-1101-jdbc41')
@Grab('commons-cli:commons-cli:1.2')
import org.apache.commons.cli.Options
import org.apache.commons.cli.PosixParser
import org.apache.commons.cli.HelpFormatter

import groovy.sql.Sql

def parseArgs = { cmdArgs ->
	def opt = new Options()
	opt.addOption('c', 'config', true, 'config file')
	opt.addOption('t', 'table', true, 'table name')

	opt.addOption('h', 'help', false, 'help')

	def res = new PosixParser().parse(opt, cmdArgs)

	if (res.hasOption('h') || !res.hasOption('t')) {
		new HelpFormatter().printHelp('db', opt, true)
		System.exit(0)
	}
	res
}

def createDb = { info ->
	Sql.newInstance(
		info.db_url,
		info.db_user,
		info.db_pass,
		info.db_driver
	)
}

def cmdLine = parseArgs(args)

def configFile = cmdLine.hasOption('c')? cmdLine.getOptionValue('c'): 'config.properties'

def setting = new Properties()
setting.load(new File(configFile).newInputStream())

def db = createDb(setting)
def table = cmdLine.getOptionValue('t')

def st = db.connection.createStatement()

def rs = st.executeQuery("select * from $table where 0 = 1")

def md = rs.metaData

(1..md.columnCount).each {
	println "${md.getColumnName(it)}, ${md.getColumnTypeName(it)}, ${md.getColumnClassName(it)}"
}
