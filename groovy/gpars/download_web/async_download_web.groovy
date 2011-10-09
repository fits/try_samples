import groovyx.gpars.*
import static groovyx.gpars.GParsPoolUtil.*
import java.nio.file.Paths
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import static java.nio.file.StandardOpenOption.*

if (args.length < 1) {
	println "${new File(System.getProperty('script.name')).name} <output dir> [<parallel num>]"
	return
}

def dir = args[0]

GParsPool.withPool {
	def res = []

	System.in.readLines() each {
		def url = new URL(it)
		def file = new File(dir, new File(url.file).name)

		def stream = { url.newInputStream() }.callAsync()
		def readStream = { stream.get().bytes }.callAsync()
		res << {
			file.bytes = readStream.get()
			[url, file]
		}.callAsync()
	}

	res.each {
		try {
			def ret = it.get()
			println "downloaded: ${ret[0]} => ${ret[1]}"
		} catch(e) {
			println "failed: $e"
		}
	}
}
