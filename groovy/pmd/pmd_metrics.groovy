@Grab('net.sourceforge.pmd:pmd-java:5.2.3')
import net.sourceforge.pmd.PMD
import net.sourceforge.pmd.PMDConfiguration
import net.sourceforge.pmd.RuleContext
import net.sourceforge.pmd.RuleSetFactory
import net.sourceforge.pmd.SourceCodeProcessor

def src = args[0]

def config = new PMDConfiguration()
def ctx = new RuleContext()

def ruleSets = new RuleSetFactory().createRuleSets('java-codesize')

def processor = new SourceCodeProcessor(config)

def report = PMD.setupReport(ruleSets, ctx, src)

ruleSets.start(ctx)

processor.processSourceCode(new File(src).newInputStream(), ruleSets, ctx)

ruleSets.end(ctx)

println '----- metrics -----'

report.metrics?.each {
	println "name: ${it.metricName}, count=${it.count}, total=${it.total}, low=${it.lowValue}, high=${it.highValue}"
}

println '----- violations -----'

report.violations?.each {
	println "class: ${it.packageName}.${it.className}, method: ${it.methodName}, description: ${it.description}"
}
