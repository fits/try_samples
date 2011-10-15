import groovyx.gpars.*

if (args.length < 1) {
	println "${new File(System.getProperty('script.name')).name} <output dir> [<parallel num>]"
	return
}


def dir = args[0]

GParsPool.withPool(10) {
//GParsExecutorsPool.withPool {
	def openUrl = { it.newInputStream() }.async()
	def downloadUrl = { f, ou -> f.bytes = ou.get().bytes }.async()

	System.in.readLines() collect {
		def url = new URL(it)
		def file = new File(dir, new File(url.file).name)

		[url: url, file: file, result: downloadUrl(file, openUrl(url))]
	} each {
// eachParallel ‚ðŽg‚¤‚Æ‘S‚Ä‚Ìˆ—‚ªŠ®—¹‚·‚é‚Ü‚Åo—Í‚³‚ê‚È‚­‚È‚é
//	} eachParallel {
		try {
			it.result.get()
			println "downloaded: ${it.url} => ${it.file}"
		} catch(e) {
			println "failed: ${it.url}, $e"
		}
	}
}
