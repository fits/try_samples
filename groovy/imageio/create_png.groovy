
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

def width = 100
def height = 100

def img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

(0..<width).each { x ->
	(0..<height).each { y ->
		def v = (Math.random() * 255) as int

		img.setRGB(x, y, (v << 16 | v << 8 | v))
	}
}

ImageIO.write(img, "png", new File("sample.png"));
