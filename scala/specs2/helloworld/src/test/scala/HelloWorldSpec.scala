import org.specs2.mutable._

class HelloWorldSpec extends Specification {

	"Hello World" should {
		"HelloWorld.msg is 'hello world'" in {
			HelloWorld.msg must be equalTo("hello world!")
		}
	}
}