package sample

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

trait Data
case class Sample1(s: String, v: Option[Int]) extends Data
case class Sample2(s: String, items: List[Sample1]) extends Data

object SampleApp extends App {
  val data1 = Sample1("t1", Some(10))
  val json1 = data1.asJson.noSpaces
  val dec1 = decode[Sample1](json1)

  println(json1)
  println(dec1)

  println("-----")

  val data2 = Sample1("t2", None)
  val json2 = data2.asJson.noSpaces
  val dec2 = decode[Sample1](json2)

  println(json2)
  println(dec2)

  println("-----")

  val data3 = Sample2("t3", List(data1, data2))
  val json3 = data3.asJson.noSpaces
  val dec3 = decode[Sample2](json3)

  println(json3)
  println(dec3)
}
