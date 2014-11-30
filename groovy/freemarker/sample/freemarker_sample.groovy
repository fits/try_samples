@Grab('org.freemarker:freemarker:2.3.21')
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler

def conf = new Configuration(Configuration.VERSION_2_3_21)

conf.directoryForTemplateLoading = new File('./template')
conf.defaultEncoding = 'UTF-8'
//conf.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER

def template = conf.getTemplate(args[0])

def data = [
	name: 'sample1',
	value: 10,
	items: [
		[id: 1, name: 'item1'],
		[id: 2, name: 'item2'],
		[id: 3, name: 'item3']
	],
	note: null,
	proc: { a, b -> a + b }
]

def sw = new StringWriter()
template.process(data, sw)

println sw.toString()
