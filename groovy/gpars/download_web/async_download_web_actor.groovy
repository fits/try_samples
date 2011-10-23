import groovyx.gpars.actor.*

if (args.length < 1) {
	println "${new File(System.getProperty('script.name')).name} <output dir> [<parallel num>]"
	return
}


def dir = args[0]

System.in.readLines() collect {u ->
	def url = new URL(u)
	def file = new File(dir, new File(url.file).name)

	def openUrl = Actors.actor {
		react {
			try {
				reply it.openStream()
			} catch (e) {
				reply e
			}
		}
	}

	def downloadUrl = Actors.actor {
		react {stream ->
			try {
				if (stream instanceof Throwable) {
					throw stream
				}

				file.bytes = stream.bytes
				println "downloaded ${url} => ${file}"
			} catch (e) {
				println "failed: ${url}, ${e}"
			}
		}
	}

	openUrl.send url, downloadUrl
	downloadUrl
} each {
	it.join()
}

