import groovyx.gpars.*
import java.nio.file.Paths
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import static java.nio.file.StandardOpenOption.*

if (args.length < 1) {
	println "${new File(System.getProperty('script.name')).name} <output dir> [<parallel num>]"
	return
}

def dir = args[0]

GParsExecutorsPool.withPool {
	System.in.readLines() collect {
		def url = new URL(it)
		def file = new File(dir, new File(url.file).name)

		def stream = { url.newInputStream() }.callAsync()
		def readStream = { stream.get().bytes }.callAsync()

		return {
			file.bytes = readStream.get()
			[url, file]
		}.callAsync()
	} each {
		try {
			def ret = it.get()
			println "downloaded: ${ret[0]} => ${ret[1]}"
		} catch(e) {
			println "failed: $e"
		}
	}
}
