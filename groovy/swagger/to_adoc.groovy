@Grab('io.github.swagger2markup:swagger2markup:1.3.0')
@Grab('org.slf4j:slf4j-nop:1.7.24')
@GrabExclude('org.apache.httpcomponents#httpcore')
@GrabExclude('commons-logging#commons-logging')
import io.github.swagger2markup.Swagger2MarkupConverter
import java.nio.file.Paths

def adoc = Swagger2MarkupConverter
	.from(Paths.get(args[0]))
	.build()
	.toString()

println adoc