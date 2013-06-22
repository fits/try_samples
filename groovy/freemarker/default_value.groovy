@Grab('org.freemarker:freemarker:2.3.19')
import freemarker.template.Configuration
import freemarker.template.Template

def template = '''
${name!'test'}
<#assign name="sample">
${name!'test'}

<#if point!1 gt 5>${point} gt 5</#if>
<#assign point=6>
<#if point! gt 5>${point} gt 5</#if>
<#--
	‰º‹L‚Ì‚æ‚¤‚É‚·‚é‚Æ NonBooleanException ‚ª”­¶
	<#if point!1 gt 5>${point} gt 5</#if>
-->
'''

def proc = { tpl, data ->
	def writer = new StringWriter()

	tpl.process(data, writer)

	println writer.toString()
}

def conf = new Configuration()
def tpl = new Template('sample', new StringReader(template), conf)

def writer = new StringWriter()

proc(tpl, [:])
