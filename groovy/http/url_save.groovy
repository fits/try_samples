import groovyx.gpars.GParsExecutorsPool

if (args.length < 1) {
	println "url_save.groovy <output dir> <url> [<url> ...]"
	return
}

GParsExecutorsPool.withPool {
	args.tail().eachParallel {u ->
		def url = new URL(u)

		try {
			def f = "${args[0]}/${url.file.split('/').last()}"

			new File(f).withOutputStream {output ->
				url.withInputStream {input ->
					output.bytes = input.bytes
				}
			}
		}
		catch (e) {
			println "failed: $url : $e"
		}
	}
}
