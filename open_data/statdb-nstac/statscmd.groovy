@Grab("org.apache.httpcomponents:httpclient:4.3")
@Grab('commons-cli:commons-cli:1.2')
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient

import org.apache.commons.cli.Options
import org.apache.commons.cli.PosixParser
import org.apache.commons.cli.HelpFormatter

import static org.apache.commons.cli.OptionBuilder.*

/**
 * 次世代統計利用システム API を使用するスクリプト
 */

def enc = 'UTF-8'
def baseUrl = 'http://statdb.nstac.go.jp/api/1.0b/app'

def settings = [
	statslist: [
		method: 'getStatsList',
		parameter: 'statsCode=%s'
	],
	statsdata: [
		method: 'getStatsData',
		parameter: 'statsDataId=%s'
	]
]

def opt = new Options()

opt.addOption('a', 'appid', true, 'Application ID')
opt.addOption('o', 'output', true, 'output file')

opt.addOption('h', 'help', false, 'help')

opt.addOption(
	withLongOpt('statslist').withDescription('get statsList').hasArgs().withArgName('statsCode').create('l')
)

opt.addOption(
	withLongOpt('statsdata').withDescription('get statsData').hasArgs().withArgName('statsDataId').create('d')
)


def cmdLine = new PosixParser().parse(opt, args)

def appId = cmdLine.getOptionValue('a')

if (!appId || cmdLine.hasOption('h')) {
	new HelpFormatter().printHelp('statscmd', opt, true)
	return
}

settings.each { k, v ->
	if (cmdLine.hasOption(k)) {
		def url = "${baseUrl}/${v.method}?appId=${appId}&" + String.format(v.parameter, cmdLine.getOptionValues(k))

		def outputFile = cmdLine.getOptionValue('o')

		def httpClient = new DefaultHttpClient()
		def res = httpClient.execute(new HttpGet(url))

		def data = res.entity.content.getText(enc)

		if (outputFile) {
			new File(outputFile).withWriter(enc) { it.write data }
		}
		else {
			println data
		}

		return
	}
}
