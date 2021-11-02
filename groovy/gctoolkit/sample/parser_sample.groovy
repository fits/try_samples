@Grab('com.microsoft.gctoolkit:gctoolkit-parser:2.0.4')
import com.microsoft.gctoolkit.parser.GenerationalHeapParser
import com.microsoft.gctoolkit.parser.jvm.LoggingDiary
import com.microsoft.gctoolkit.event.generational.*

def parser = new GenerationalHeapParser(
	new LoggingDiary(),
	{ event ->
		switch (event) {
			case PSYoungGen:
			case PSFullGC:

				println "timestamp=${event.dateTimeStamp}, type=${event.garbageCollectionType}, cause=${event.GCCause}, young=${event.getYoung()}, tenured=${event.tenured}, heap=${event.heap}, metaspace=${event.permOrMetaspace}, duration=${event.duration}, cpu=${event.cpuSummary}"

				break
		}
	}
)

new File(args[0]).eachLine {
	parser.receive it
}
