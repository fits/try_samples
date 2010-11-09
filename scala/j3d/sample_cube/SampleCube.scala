
import scala.swing._

import com.sun.j3d.utils.universe._
import com.sun.j3d.utils.geometry.ColorCube
import javax.media.j3d._
import javax.vecmath._
import javax.swing._

object SampleCube extends SimpleGUIApplication {

	implicit def toF(d: Double): Float = {
		d.toFloat
	}

	//Canvas3D を scala.swing.Component に変換
	implicit def toComponent(c: Canvas3D): Component = {
		val p = new JPanel(new java.awt.BorderLayout())
		p.add(c)
		Component.wrap(p)
	}

	def top = new MainFrame {
		title = "Cube sample"
		contents = createCanvas()
		size = (300, 300)
	}

	//シーングラフの作成
	def createSceneGraph(): BranchGroup = {
		val root = new BranchGroup()

		//背景色
		val bg = new Background()
		bg.setApplicationBounds(new BoundingSphere())
		bg.setColor(0.5f, 0.5f, 0.5f)

		root.addChild(bg)

		val trans = new TransformGroup()
		//回転を許可
		trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE)

		root.addChild(trans)

		trans.addChild(new ColorCube(0.4))

		//回転
		val rotator = new RotationInterpolator(new Alpha(-1, 5000), trans, new Transform3D(), 0.0f, Math.Pi * 2.0f)
		rotator.setSchedulingBounds(new BoundingSphere())

		root.addChild(rotator)

		return root
	}

	//Canvas3D の作成
	def createCanvas(): Canvas3D = {
		val c = new Canvas3D(SimpleUniverse.getPreferredConfiguration())

		val univ = new SimpleUniverse(c)
		univ.getViewingPlatform().setNominalViewingTransform()

		univ.addBranchGraph(this.createSceneGraph())

		return c
	}
}

