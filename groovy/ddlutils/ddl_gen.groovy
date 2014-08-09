@Grab('org.apache.ddlutils:ddlutils:1.0')
@Grab('org.apache.commons:commons-dbcp2:2.0.1')
@Grab('commons-cli:commons-cli:1.2')
import org.apache.ddlutils.PlatformFactory
import org.apache.ddlutils.model.TypeMap
import org.apache.commons.dbcp2.BasicDataSource
import org.apache.commons.cli.Options
import org.apache.commons.cli.PosixParser
import org.apache.commons.cli.HelpFormatter

// Oracle エラー対策（Unknown JDBC type code -101）
TypeMap._typeCodeToTypeName.put(-101, 'TIMESTAMP')

def parseArgs = { cmdArgs ->
	def opt = new Options()
	opt.addOption('c', 'config', true, 'config file')
	opt.addOption('t', 'table', true, 'table name')
	opt.addOption('d', 'db', true, 'target db')

	opt.addOption('h', 'help', false, 'help')

	def res = new PosixParser().parse(opt, cmdArgs)

	if (res.hasOption('h') || !res.hasOption('d')) {
		new HelpFormatter().printHelp('ddl_gen', opt, true)
		System.exit(0)
	}
	res
}

def createDs = { info -> new BasicDataSource(url: info.db_url, username: info.db_user, password: info.db_pass, driverClassName: info.db_driver) }

def cmdLine = parseArgs(args)

def configFile = cmdLine.hasOption('c')? cmdLine.getOptionValue('c'): 'config.properties'

def setting = new Properties()
setting.load(new File(configFile).newInputStream())

def filter = cmdLine.hasOption('t')? { t -> 
	cmdLine.getOptionValues('t').collect { it.toLowerCase() }.contains( t.name.toLowerCase() )
}: { t -> true }

def src = PlatformFactory.createNewPlatformInstance(createDs(setting))
def trg = PlatformFactory.createNewPlatformInstance(cmdLine.getOptionValue('d'))

def dbModel = src.readModelFromDatabase(setting.db_type)

def writer = new StringWriter()

def builder = trg.sqlBuilder
builder.writer = writer

dbModel.tables.findAll(filter).each {
	builder.createTable(dbModel, it)
}

writer.close()

println writer.toString()
