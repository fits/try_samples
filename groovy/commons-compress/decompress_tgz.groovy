@Grab('org.apache.commons:commons-compress:1.5')
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

/*
 * .tar.gz や .tgz ファイルを解凍するサンプルスクリプト
 */

def parent = (args.length > 1)? args[1]: '.'

def parentDir = new File(parent)

def factory = new ArchiveStreamFactory()

def f = new File(args[0])

f.withInputStream {

	def ais = factory.createArchiveInputStream('tar', new GzipCompressorInputStream(it))

	def entry = null

	while((entry = ais.nextEntry) != null) {
		println entry.name

		if (entry.isDirectory()) {
			new File(parentDir, entry.name).mkdirs()
		}
		else if (entry.isFile()) {
			def destFile = new File(parentDir, entry.name)

			if (!destFile.parentFile.exists()) {
				destFile.parentFile.mkdirs()
			}

			destFile.withOutputStream { output ->
				def buf = new byte[entry.size]
				def offset = 0
				def len = 0

				while((len = ais.read(buf, offset, buf.length - offset)) > -1) {
					offset += len
					output.write(buf, 0, len)
				}
			}
		}
	}

	ais.close()
}
