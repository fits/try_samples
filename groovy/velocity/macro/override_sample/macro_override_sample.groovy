@Grab('org.apache.velocity:velocity:1.7')
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity

/**
 * Velocity で macro のオーバーライドを確認するサンプル
 */

Velocity.init('velocity.properties')

def template = Velocity.getTemplate(args[0])

def context = new VelocityContext()

System.out.withWriter {
	template.merge(context, it)
}
