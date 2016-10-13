
import static java.time.format.DateTimeFormatter.*
import java.time.format.DateTimeFormatterBuilder

def trycatch = { proc ->
	try {
		proc()
	} catch(e) {
		println "*** ERROR: $e"
	}
}

def parser = new DateTimeFormatterBuilder()
					.parseCaseInsensitive()
					.parseLenient()
					.appendInstant()
					.toFormatter(Locale.ROOT)

trycatch { println "instant: ${parser.parse(args[0])}" }

trycatch {
	println "ISO_LOCAL_DATE_TIME: ${ISO_LOCAL_DATE_TIME.parse(args[0])}"
}

trycatch {
	println "ISO_OFFSET_DATE_TIME: ${ISO_OFFSET_DATE_TIME.parse(args[0])}"
}

trycatch {
	println "ISO_LOCAL_DATE: ${ISO_LOCAL_DATE.parse(args[0])}" 
}
