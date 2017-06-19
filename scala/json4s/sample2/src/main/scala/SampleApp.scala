
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

object SampleApp extends App {

  val json = ("name" -> "sample1") ~ ("value" -> 123)

  println( compact(render(json)) )
}
