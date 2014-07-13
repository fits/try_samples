@GrabConfig(systemClassLoader = true)
@Grab('org.postgresql:postgresql:9.3-1101-jdbc41')
@Grab('net.sf.supercsv:super-csv:2.2.0')
@Grab('commons-cli:commons-cli:1.2')
import org.supercsv.io.CsvMapReader
import org.supercsv.prefs.CsvPreference

import org.apache.commons.cli.Options
import org.apache.commons.cli.PosixParser
import org.apache.commons.cli.HelpFormatter

import groovy.sql.Sql
import groovy.sql.DataSet

def parseArgs = { cmdArgs ->
	def opt = new Options()
	opt.addOption('c', 'config', true, 'config file')
	opt.addOption('f', 'file', true, 'csv file')
	opt.addOption('t', 'table', true, 'table name')

	opt.addOption('h', 'help', false, 'help')

	def res = new PosixParser().parse(opt, cmdArgs)

	if (res.hasOption('h') || !res.hasOption('t') || !res.hasOption('f')) {
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

def columnTypeMapping = { connection, tableName ->
	def st = connection.createStatement()
	def rs = st.executeQuery("select * from $tableName where 0 = 1")

	def md = rs.metaData
	def result = [:]

	(1..md.columnCount).each { i ->
		def cls = Class.forName(md.getColumnClassName(i))
		def proc = { String v -> v.asType(cls) }

		if (java.util.Date.isAssignableFrom(cls)) {
			// 変換の厳密性はとりあえず無視
			proc = { String v -> cls.newInstance(Date.parse('yyyy-MM-dd HH:mm:ss', v).time) }
		}

		result[md.getColumnName(i).toLowerCase()] = proc
	}

	rs.close()
	st.close()
	result
}

def cmdLine = parseArgs(args)

def configFile = cmdLine.hasOption('c')? cmdLine.getOptionValue('c'): 'config.properties'

def setting = new Properties()
setting.load(new File(configFile).newInputStream())

def db = createDb(setting)

def csvFile = cmdLine.getOptionValue('f');
def table = cmdLine.getOptionValue('t');

def csv = new CsvMapReader(new File(csvFile).newReader(setting.file_encode), CsvPreference.STANDARD_PREFERENCE)

def headers = csv.getHeader(true)

def res = null

def mapping = columnTypeMapping(db.connection, table)

def dataSet = new DataSet(db, table)

dataSet.withTransaction {
	while((res = csv.read(headers)) != null) {

		def d = res.collectEntries { k, v ->
			def nv = (v == null)? v: mapping[k.toLowerCase()].call(v)
			[ k, nv ]
		}

		dataSet.add(d)
	}
}
