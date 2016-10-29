
import org.json4s.{ DefaultFormats, Extraction }
import org.json4s.jackson.JsonMethods._

case class Data(name: String, value: Int)

object SampleApp extends App {

  implicit val formats = DefaultFormats

  val json = parse("""
    {"name": "sample1", "value": 123}
  """)

  println( json.extract[Data] )

  println( compact(Extraction.decompose(Data("sample2", 456))) )
}
