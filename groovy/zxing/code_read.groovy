@Grab('com.google.zxing:javase:3.2.1')
import com.google.zxing.MultiFormatReader
import com.google.zxing.BinaryBitmap
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.client.j2se.BufferedImageLuminanceSource

import javax.imageio.ImageIO

def reader = new MultiFormatReader()

def img = ImageIO.read(new File(args[0]))

def result = reader.decode(
	new BinaryBitmap(
		new HybridBinarizer(
			new BufferedImageLuminanceSource(img)
		)
	)
)

println "code: ${result.text}, format: ${result.barcodeFormat}"
