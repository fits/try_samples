import groovyx.gpars.GParsExecutorsPool

if (args.length < 1) {
	println "${new File(System.getProperty('script.name')).name} <output dir> [<parallel num>]"
	return
}

def num = (args.length > 1)? args[1].toInteger(): 5

GParsExecutorsPool.withPool(num) {
	System.in.readLines() eachParallel {u ->
		def url = new URL(u)
		def f = "${args[0]}/${url.file.split('/').last()}"

		try {
			url.withInputStream {input ->
				new File(f).bytes = input.bytes
			}

			println "downloaded: $url => $f"
		}
		catch (e) {
			println "failed: $url"
		}
	}
}
