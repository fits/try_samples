@Grab('org.thymeleaf:thymeleaf:2.1.4.RELEASE')
@Grab('org.slf4j:slf4j-nop:1.7.10')
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templateresolver.FileTemplateResolver

def engine = new TemplateEngine()
engine.addTemplateResolver(new FileTemplateResolver())

def ctx = new Context()
ctx.variables = [
	'items': [
		[name: 'sample', point: 10],
		[name: 'test', point: 100],
		[name: 'one', point: 50]
	]
]

println engine.process(args[0], ctx)

