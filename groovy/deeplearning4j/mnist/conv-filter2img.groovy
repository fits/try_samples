@Grab('org.deeplearning4j:deeplearning4j-core:0.4-rc3.8')
@Grab('org.nd4j:nd4j-x86:0.4-rc3.8')
@Grab('com.twelvemonkeys.imageio:imageio-core:3.2.1')
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.api.Layer
import org.nd4j.linalg.indexing.*

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

def modelFile = args[0]
def imgType = 'png'

def loadModel = { f -> 
	new File(f).withObjectInputStream(this.class.classLoader) {
		it.readObject() as MultiLayerNetwork
	}
}

def normalize = { w ->
	w = w.sub(w.minNumber())
	w.div(w.maxNumber())
}

def model = loadModel(modelFile)

model.layers.findAll { it.type() == Layer.Type.CONVOLUTIONAL }.each {
	def w = normalize(it.paramTable().W)
	def shape = w.shape()

	def blockWidth = shape[3] + 1

	def img = new BufferedImage(
		shape[0] * blockWidth, 
		shape[2], 
		BufferedImage.TYPE_INT_RGB
	)

	(0..<shape[0]).each { s ->
		def data = w.get(new PointIndex(s)).get(new PointIndex(0))

		def offset = s * blockWidth

		(0..<data.rows()).each { r ->
			(0..<data.columns()).each { c ->
				def v = data.getDouble(r, c) * 255 as int

				img.setRGB(c + offset, r, (v << 16 | v << 8 | v))
			}

			img.setRGB(data.columns() + offset, r, 0x00ff00)
		}
	}

	def f = new File("${new File(modelFile).name}.${it.index}.${imgType}")

	ImageIO.write(img, imgType, f)
}
