@Grapes([
	@Grab('io.github.swagger2markup:swagger2markup:1.3.0'),
	@GrabExclude('org.apache.httpcomponents#httpcore'),
	@GrabExclude('commons-logging#commons-logging')
])
@Grapes([
	@Grab('org.asciidoctor:asciidoclet:1.5.4'),
	@GrabExclude('org.slf4j#slf4j-simple')
])
import io.github.swagger2markup.Swagger2MarkupConverter
import org.asciidoctor.Asciidoctor.Factory

import java.nio.file.Paths

def adoc = Swagger2MarkupConverter
	.from(Paths.get(args[0]))
	.build()
	.toString()

def asciidoctor = Factory.create()

def html = asciidoctor.convert(adoc, [:])

new File(args[1]).write(html, 'UTF-8')
