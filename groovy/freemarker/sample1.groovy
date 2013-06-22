@Grab('org.freemarker:freemarker:2.3.19')
import freemarker.template.Configuration
import freemarker.template.Template

// テンプレート定義
def template = '''name: ${name}, point: ${point}<#if point gt 10>
great!</#if>'''

def proc = { tpl, data ->
	def writer = new StringWriter()

	tpl.process(data, writer)

	println writer.toString()
}

def conf = new Configuration()
def tpl1 = new Template('sample', new StringReader(template), conf)

def writer = new StringWriter()

proc(tpl1, [
	name: 'sample1',
	point: 5
])

proc(tpl1, [
	name: 'テスト2',
	point: 15
])

