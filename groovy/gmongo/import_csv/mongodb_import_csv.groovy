@Grab('com.gmongo:gmongo:1.3')
@Grab('net.sf.supercsv:super-csv:2.2.0')
@Grab('commons-cli:commons-cli:1.2')
import com.gmongo.*
import org.supercsv.io.CsvMapReader
import org.supercsv.prefs.CsvPreference

import org.apache.commons.cli.Options
import org.apache.commons.cli.PosixParser
import org.apache.commons.cli.HelpFormatter

def parseArgs = { cmdArgs ->
	def opt = new Options()
	opt.addOption('c', 'config', true, 'config file')
	opt.addOption('f', 'file', true, 'csv file')
	opt.addOption('t', 'table', true, 'collection name')

	opt.addOption('h', 'help', false, 'help')

	def res = new PosixParser().parse(opt, cmdArgs)

	if (res.hasOption('h') || !res.hasOption('t') || !res.hasOption('f')) {
		new HelpFormatter().printHelp('db', opt, true)
		System.exit(0)
	}
	res
}

def converters = [
	/^[0-9]{4}-[0-9]{2}-[0-9]{2}/ : { Date.parse('yyyy-MM-dd HH:mm:ss', it) },
	/^[0-9]+$/ : { it as int }
]

def convertValue = { v ->
	def result = converters.inject(null) { res, ptn, proc ->
		if (res == null) {
			def m = v =~ ptn

			if (m) {
				res = proc(v)
			}
		}
		res
	}
	(result == null)? v: result
}

def cmdLine = parseArgs(args)

def configFile = cmdLine.hasOption('c')? cmdLine.getOptionValue('c'): 'config.properties'

def setting = new Properties()
setting.load(new File(configFile).newInputStream())

def mongo = new GMongo()
def db = mongo.getDB(setting.db_name)

def csvFile = cmdLine.getOptionValue('f');
def table = cmdLine.getOptionValue('t');

def csv = new CsvMapReader(new File(csvFile).newReader(setting.file_encode), CsvPreference.STANDARD_PREFERENCE)

def headers = csv.getHeader(true)

def res = null

while((res = csv.read(headers)) != null) {
	def data = res.collectEntries { k, v ->
		[k.toLowerCase(), convertValue(v)]
	}

//	println data
	db[table] << data
}
