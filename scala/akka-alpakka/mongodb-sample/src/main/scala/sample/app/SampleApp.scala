package sample.app

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.mongodb.scaladsl.MongoSource
import org.mongodb.scala.MongoClient
import org.mongodb.scala.bson.collection.immutable.Document

import scala.concurrent.Await
import scala.concurrent.duration._

object SampleApp extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val client = MongoClient()

  val db = client.getDatabase("test")
  val col = db.getCollection("sample")

  val docs = Range(0, 5).map { n => Document("name" -> s"item$n", "value" -> (n * 2)) }

  val res = Await.result(col.insertMany(docs).toFuture(), 5 seconds)

  println(res)

  println("-----")

  col.find().foreach(println)

  println("-----")

  val res2 = MongoSource(col.find()).map { d =>
    s"name: ${d.get("name")}, value: ${d.get("value")}"
  }.runForeach(println)

  Await.result(res2, 5 seconds)

  client.close()
  system.terminate()
}
