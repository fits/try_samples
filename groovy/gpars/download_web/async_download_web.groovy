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
		def future = { file.bytes = readStream.get() }.callAsync()

		[url: url, file: file, future: future]
	} each {
		try {
			it.future.get()
			println "downloaded: ${it.url} => ${it.file}"
		} catch(e) {
			println "failed: ${it.url}, $e"
		}
	}
}
