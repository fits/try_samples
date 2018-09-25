package sample.app

import org.mongodb.scala.{MongoClient, MongoCollection}
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}

import scala.concurrent.Await
import scala.concurrent.duration._

case class Data(_id: ObjectId, name: String, value: Int)

object Data {
  def apply(name: String, value: Int): Data = Data(new ObjectId, name, value)
}

object SampleApp extends App {
  val codecRegistry = fromRegistries(fromProviders(classOf[Data]), DEFAULT_CODEC_REGISTRY)

  val client = MongoClient()

  val db = client.getDatabase("test").withCodecRegistry(codecRegistry)
  val col: MongoCollection[Data] = db.getCollection("sample")

  val docs = Range(0, 5).map { n => Data(s"item$n", n * 3) }

  val res = Await.result(col.insertMany(docs).toFuture(), 5 seconds)

  println(res)

  println("-----")

  Await.result(col.find().toFuture(), 5 seconds).foreach(println)
}
