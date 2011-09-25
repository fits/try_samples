import groovyx.gpars.GParsExecutorsPool

GParsExecutorsPool.withPool {
	System.in.readLines() eachParallel {u ->
		def url = new URL(u)
		def filePath = "${args[0]}/${url.file.split('/').last()}"

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
