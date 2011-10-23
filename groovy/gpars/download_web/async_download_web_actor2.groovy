import groovyx.gpars.actor.*

if (args.length < 1) {
	println "${new File(System.getProperty('script.name')).name} <output dir> [<parallel num>]"
	return
}


class DownloadActor extends DynamicDispatchActor {
	def dir
	def url

	void onMessage(String u) {
		url = new URL(u)
		send url.openStream()
	}

	void onMessage(InputStream stream) {
		def file = new File(dir, new File(url.file).name)

		file.bytes = stream.bytes
		println "downloaded: ${url} => ${file}"

		stop()
	}

	void onException(e) {
		println "failed: ${url}, ${e}"
	}
}

System.in.readLines() collect {u ->
	def actor = new DownloadActor(dir: args[0])

	actor.start()
	actor u

	actor
} each {
	it.join()
}
