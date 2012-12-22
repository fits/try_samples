@Grapes([
	@Grab('org.eclipse.birt.runtime:org.eclipse.birt.runtime:4.2.1'),
	@Grab('org.eclipse.birt.runtime:org.eclipse.core.runtime:3.8.0.v20120521-2346'),
	@GrabExclude('org.milyn#flute;1.3'),
	@GrabExclude('commons-logging#commons-logging;1.0!commons-logging.jar')
])
import org.eclipse.birt.report.engine.api.*

if (args.length < 2) {
	println "<rptdesign template> <output file>"
	return
}

def rptdesignFile = args[0]
def outputFile = args[1]

def engine = new ReportEngine(new EngineConfig())

new File(rptdesignFile).withInputStream {
	def design = engine.openReportDesign(it)

	def task = engine.createRunAndRenderTask(design)

	def options = new PDFRenderOption()
	options.outputFileName = outputFile
	options.outputFormat = RenderOption.OUTPUT_FORMAT_PDF

	task.renderOption = options

	task.run()
}

engine.destroy()
