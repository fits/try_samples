@Grab('org.nd4j:nd4j-x86:0.4-rc3.8')
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.dataset.api.DataSet
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.util.FeatureUtil

import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Paths

abstract class MnistLoader extends Script {
	def LABELS_NUM = 10

	def loadMnistImages(String imageFileName) {
		FileChannel.open(Paths.get(imageFileName)).withCloseable { fc ->

			def hbuf = ByteBuffer.allocateDirect(16)

			fc.read(hbuf)

			hbuf.rewind()

			def magicNum = hbuf.getInt()
			def num = hbuf.getInt()
			def rowNum = hbuf.getInt()
			def colNum = hbuf.getInt()

			def buf = ByteBuffer.allocateDirect(num * rowNum * colNum)

			fc.read(buf)

			buf.rewind()

			def size = rowNum * colNum

			(0..<num).inject( Nd4j.create(num, size) ) { a, n ->
				def d = (0..<size).inject( Nd4j.create(1, size) ) { s, i ->
					s.putScalar(i, buf.get() & 0xff)
					s
				}

				a.putRow(n, d)
				a
			}
		}
	}

	def loadMnistLabels(String labelFileName) {
		FileChannel.open(Paths.get(labelFileName)).withCloseable { fc ->
			def hbuf = ByteBuffer.allocateDirect(8)

			fc.read(hbuf)

			hbuf.rewind()

			def magicNum = hbuf.getInt()
			def num = hbuf.getInt()

			def buf = ByteBuffer.allocateDirect(num)

			fc.read(buf)

			buf.rewind()

			(0..<num).inject( Nd4j.create(num, LABELS_NUM) ) { a, i ->
				a.putRow(i, FeatureUtil.toOutcomeVector(buf.get(), LABELS_NUM))
				a
			}
		}
	}

	def loadMnist(imageFileName, labelFileName) {
		groovyx.gpars.GParsPool.withPool(2) {
			def imagesFt = { loadMnistImages(it) }.async()(imageFileName)
			def labelsFt = { loadMnistLabels(it) }.async()(labelFileName) 

			new org.nd4j.linalg.dataset.DataSet(imagesFt.get(), labelsFt.get())
		}
	}
}