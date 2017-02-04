@Grab('net.sourceforge.pmd:pmd-java:5.5.3')
import net.sourceforge.pmd.PMD
import net.sourceforge.pmd.PMDConfiguration
import net.sourceforge.pmd.RuleContext
import net.sourceforge.pmd.RuleSetFactory
import net.sourceforge.pmd.lang.LanguageFilenameFilter
import net.sourceforge.pmd.util.FileUtil
import net.sourceforge.pmd.SourceCodeProcessor

def dir = args[0]

def config = new PMDConfiguration()
config.setRuleSets('java-codesize')

def ctx = new RuleContext()

def processor = new SourceCodeProcessor(config)

def languages = new RuleSetFactory().createRuleSets(config.ruleSets).allRules*.language as Set
def fileFilter = new LanguageFilenameFilter(languages)

def fileNameFrom = {
	it.getNiceFileName(true, dir)
}

def reports = FileUtil.collectFiles(dir, fileFilter).collectEntries {
	def fileName = fileNameFrom(it)

	def ruleSets = new RuleSetFactory().createRuleSets(config.ruleSets)

	def report = PMD.setupReport(ruleSets, ctx, fileName)

	processor.processSourceCode(it.inputStream, ruleSets, ctx)

	[fileName, report]
}


reports.each { k, v ->

	println "----- ${k} -----"

	v.metrics?.sort{ it.metricName }.each {
		println "name: ${it.metricName}, count=${it.count}, total=${it.total}, low=${it.lowValue}, high=${it.highValue}"
	}

	v.violations?.each {
		println "class: ${it.packageName}.${it.className}, method: ${it.methodName}, description: ${it.description}"
	}

	println ''
}
