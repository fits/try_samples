import org.specs._

class HelloWorldSpec extends Specification {
	"HelloWorld.msg is 'hello world'" in {
		HelloWorld.msg must_== "hello world!"
	}
}