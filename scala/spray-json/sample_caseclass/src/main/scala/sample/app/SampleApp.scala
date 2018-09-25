package sample.app

import spray.json.DefaultJsonProtocol

case class Data(name: String, value: Int)

object SampleJsonProtocol extends DefaultJsonProtocol {
  implicit val dataFormat = jsonFormat2(Data)
}

object SampleApp extends App {
  import SampleJsonProtocol._
  import spray.json._

  val json = Data("item1", 10).toJson

  println(json)

  val d = json.convertTo[Data]

  println(d)
}
