
import org.specs._

object PositionSpec extends Specification {

	"Position(10, 5)" should {
		val p = Position(10, 5)

		"x は 10" in {
			p.x must be(10)
		}
		"y は 5" in {
			p.y must be(5)
		}
	}

}

