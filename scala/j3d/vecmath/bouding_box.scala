
import javax.media.j3d._
import com.sun.j3d.utils.geometry.Cone

val c1 = new Cone(10, 5)

val body = c1.getShape(Cone.BODY)
println("body: " + body.getBounds)

val cap = c1.getShape(Cone.CAP)
println("cap: " + cap.getBounds)

