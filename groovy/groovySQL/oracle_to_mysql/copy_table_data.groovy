/**
 * Oracle からテーブルの内容を MySQL へコピーするスクリプト
 */
@Grab('commons-cli:commons-cli:1.2')
import org.apache.commons.cli.Options
import org.apache.commons.cli.PosixParser
import org.apache.commons.cli.HelpFormatter

import groovy.sql.*

def parseArgs = { cmdArgs ->
	def opt = new Options()
	opt.addOption('c', 'config', true, 'config file')
	opt.addOption('t', 'table', true, 'table name')

	opt.addOption('h', 'help', false, 'help')

	def res = new PosixParser().parse(opt, cmdArgs)

	if (res.hasOption('h') || !res.hasOption('t')) {
		new HelpFormatter().printHelp('copy_table_data', opt, true)
		System.exit(0)
	}
	res
}

def convert = { r ->
	r.collectEntries { k, v ->
		if (v instanceof oracle.sql.TIMESTAMP) {
			v = v.stringValue()
		}

		[k, v]
	}
}

def createDb = { url, user, pass, driver -> Sql.newInstance(url, user, pass, driver) }

def createSelectSql = { name -> "select * from ${name}" as String }

def createInsertSql = { name, columns, keys ->
	"""
		insert into ${name} ( ${columns.join(',')} )
			values ( ${columns.collect{":$it"}.join(',')} )
		on duplicate key update
			${(columns - keys).collect{ "$it = :$it" }.join(',')}
	"""
}

def columnDataList = { String column, java.sql.ResultSet rs ->
	def result = []

	while (rs.next()) {
		result << rs.getString(column)
	}
	result
}


def cmdLine = parseArgs(args)

def configFile = cmdLine.hasOption('c')? cmdLine.getOptionValue('c'): 'config.properties'

def setting = new Properties()
setting.load(new File(configFile).newInputStream())

def table = cmdLine.getOptionValue('t')

def srcDb = createDb(setting.db1_url, setting.db1_user, setting.db1_pass, setting.db1_driver)

def trgDb = createDb(setting.db2_url, setting.db2_user, setting.db2_pass, setting.db2_driver)

def columnNames = columnDataList.curry('COLUMN_NAME')

def md = trgDb.connection.metaData

def insertSql = createInsertSql(
	table, 
	columnNames(md.getColumns(null, null, table, '%')),
	columnNames(md.getPrimaryKeys(null, null, table))
)

trgDb.withTransaction {
	trgDb.withBatch(setting.batch_size as int, insertSql) { ps ->

		srcDb.rows( createSelectSql(table) ).each { r ->
			ps.addBatch(convert(r))
		}

	}
}
