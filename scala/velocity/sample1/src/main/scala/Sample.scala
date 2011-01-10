package fits.sample

import java.io.StringWriter
import org.apache.velocity.{VelocityContext, Template}
import org.apache.velocity.app.Velocity

object Sample {
	def main(args: Array[String]) {
		Velocity.init()

		val ctx = new VelocityContext()
		val temp = Velocity.getTemplate("sample.vm")

		val sw = new StringWriter()
		temp.merge(ctx, sw)

		println(sw)
	}
}