import groovyx.gpars.*

def dir = args[0]

GParsExecutorsPool.withPool {
	//URL接続処理
	def openUrl = { it.newInputStream() }.async()
	//ダウンロード処理
	def downloadUrl = { f, ou -> f.bytes = ou.get().bytes }.async()

	System.in.readLines() collect {
		def url = new URL(it)
		def file = new File(dir, new File(url.file).name)

		[url: url, file: file, result: downloadUrl(file, openUrl(url))]

	} eachParallel {
		try {
			it.result.get()
			println "downloaded: ${it.url} => ${it.file}"
		} catch(e) {
			println "failed: ${it.url}, $e"
		}
	}
}
