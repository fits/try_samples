@Grab('commons-cli:commons-cli:1.2')
import org.apache.commons.cli.Options
import org.apache.commons.cli.PosixParser
import org.apache.commons.cli.HelpFormatter

import static org.apache.commons.cli.OptionBuilder.*

def opt = new Options()
// ˆø”–³‚µ
opt.addOption('t', 'test', false, 'test option')
// ˆø”•t‚«
opt.addOption('f', 'file', true, 'file option')

opt.addOption(
	withLongOpt('category').withDescription('category option').create('c')
)

opt.addOption(
	withLongOpt('add').withDescription('data add').hasArgs(3).withArgName('args1 args2 args3').create('a')
)

def cmdLine = new PosixParser().parse(opt, args)

println "args = ${cmdLine.args}"

println "t option = ${cmdLine.hasOption('t')}"
println "f value = ${cmdLine.getOptionValue('f')}"
// 1‚Â‚Ìˆø”‚ğ valueSeparator ‚Å•ª—£
println "f values = ${cmdLine.getOptionValues('f')}"

println "a values = ${cmdLine.getOptionValues('a')}"

println "-----"

println cmdLine.options


println "-----"

new HelpFormatter().printHelp('sample', opt, true)

println "-----"

new HelpFormatter().printHelp('sample [options] args1 args2', opt)
