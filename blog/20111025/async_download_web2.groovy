import groovyx.gpars.actor.*

def dir = args[0]

System.in.readLines() collect {u ->
	def download  = Actors.actor {
		def url

		//例外発生時の処理
		delegate.metaClass.onException = {
			println "failed: ${url}, ${it}"
		}

		react {urlString ->
			//URL接続 (2)
			url = new URL(urlString)
			//Actor へのメッセージ送信 (3)
			send url.openStream()

			react {stream ->
				//ダウンロード処理 (4)
				def file = new File(dir, new File(url.file).name)
				file.bytes = stream.bytes

				println "downloaded: ${url} => ${file}"
			}
		}
	}

	//Actor へのメッセージ送信 (1)
	download.send u
	download
} each {
	it.join()
}

