import groovyx.gpars.actor.*

if (args.length < 1) {
	println "${new File(System.getProperty('script.name')).name} <output dir> [<parallel num>]"
	return
}


def dir = args[0]

System.in.readLines() collect {u ->
	def download  = Actors.actor {
		def url

		delegate.metaClass.onException = {
			println "failed: ${url}, ${it}"
		}

		react {
			url = new URL(it)
			send url.openStream()

			react {stream ->
				def file = new File(dir, new File(url.file).name)
				file.bytes = stream.bytes

				println "downloaded: ${url} => ${file}"
			}
		}
	}

	download.send u
	download
} each {
	it.join()
}

