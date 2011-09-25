import groovyx.gpars.GParsExecutorsPool

def dir = args[0]

GParsExecutorsPool.withPool {
	System.in.readLines() eachParallel {u ->
		def url = new URL(u)
		def filePath = "$dir/${url.file.split('/').last()}"

		try {
			url.withInputStream {input ->
				new File(filePath).bytes = input.bytes
			}

			println "downloaded: $url => $filePath"
		}
		catch (e) {
			println "failed: $url, $e"
		}
	}
}
