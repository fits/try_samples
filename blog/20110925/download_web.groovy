import groovyx.gpars.GParsExecutorsPool

def dir = args[0]

GParsExecutorsPool.withPool {
	System.in.readLines() eachParallel {u ->
		def url = new URL(u)

		try {
			def file =  new File(dir, new File(url.file).name)

			url.withInputStream {input ->
				file.bytes = input.bytes
			}

			println "downloaded: $url => $file"
		}
		catch (e) {
			println "failed: $url, $e"
		}
	}
}
