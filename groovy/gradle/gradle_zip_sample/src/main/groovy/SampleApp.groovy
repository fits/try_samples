package fits.sample

import org.apache.commons.cli.Options
import org.apache.commons.cli.PosixParser

class SampleApp {
	static void main(args) {
		def cmdLine = parseArgs(args)

		println "args = ${cmdLine.args}"
		println "t option = ${cmdLine.hasOption('t')}"
		println "option value = ${cmdLine.getOptionValue('f')}"
	}

	static createOptions() {
		def opt = new Options()
		// 引数無し
		opt.addOption('t', false, 'test')
		// 引数付き
		opt.addOption('f', true, 'file')
	}

	static parseArgs(args) {
		new PosixParser().parse(createOptions(), args)
	}
}
