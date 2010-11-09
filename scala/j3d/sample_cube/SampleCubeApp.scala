
import com.sun.j3d.utils.universe._
import com.sun.j3d.utils.geometry.ColorCube
import java.awt.GraphicsConfiguration
import javax.media.j3d._
import javax.vecmath._
import javax.swing._

object SampleCubeApp {

	implicit def toF(d: Double): Float = {
		d.toFloat
	}

	def main(args: Array[String]) {
		val f = new JFrame()

		val c = createCanvas()

		f.getContentPane().add(c, java.awt.BorderLayout.CENTER)
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

		f.setSize(300, 300)
		f.setVisible(true)
	}

	def createSceneGraph(): BranchGroup = {
		val root = new BranchGroup()

		val trans = new TransformGroup()
		trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE)

		root.addChild(trans)

		trans.addChild(new ColorCube(0.4))

		val rotator = new RotationInterpolator(new Alpha(-1, 4000), trans, new Transform3D(), 0.0f, 3.14f * 2.0f)

		val bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0)
		rotator.setSchedulingBounds(bounds)

		root.addChild(rotator)

		root.compile()

		return root
	}

	def createCanvas(): Canvas3D = {
		val c = new Canvas3D(SimpleUniverse.getPreferredConfiguration())

		val univ = new SimpleUniverse(c)
		univ.getViewingPlatform().setNominalViewingTransform()

		univ.addBranchGraph(this.createSceneGraph())

		return c
	}
}

