@Grab('commons-cli:commons-cli:1.2')
import org.apache.commons.cli.Options
import org.apache.commons.cli.PosixParser
import org.apache.commons.cli.HelpFormatter

import groovy.sql.Sql

def parseArgs = { cmdArgs ->
	def opt = new Options()
	opt.addOption('c', 'config', true, 'config file')

	opt.addOption('h', 'help', false, 'help')

	def res = new PosixParser().parse(opt, cmdArgs)

	if (res.hasOption('h')) {
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

def eachRow = { rs, proc ->
	while(rs.next()) {
		proc(rs)
	}
	rs.close()
}

def rowString = { rs, String... columns ->
	columns.collect { rs.getString it }
}

def cmdLine = parseArgs(args)

def configFile = cmdLine.hasOption('c')? cmdLine.getOptionValue('c'): 'config.properties'

def setting = new Properties()
setting.load(new File(configFile).newInputStream())

def db = createDb(setting)

def md = db.connection.metaData

println '===== FUNCTIONSS ====='
// ファンクションの情報を取得
eachRow(md.getFunctions(null, null, "%")) {
	println rowString(it, 'FUNCTION_NAME', 'FUNCTION_TYPE', 'FUNCTION_CAT', 'FUNCTION_SCHEM', 'SPECIFIC_NAME').join(',')
}
println ''

println '===== PROCEDURES ====='
// プロシージャの情報を取得
eachRow(md.getProcedures(null, null, "%")) {
	println rowString(it, 'PROCEDURE_NAME', 'PROCEDURE_TYPE', 'PROCEDURE_CAT', 'PROCEDURE_SCHEM', 'SPECIFIC_NAME').join(',')
}
println ''
