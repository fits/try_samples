package sample.app

import org.mongodb.scala.MongoClient
import org.mongodb.scala.bson.collection.immutable.Document

import scala.concurrent.Await
import scala.concurrent.duration._

object SampleApp extends App {
  val client = MongoClient()

  val db = client.getDatabase("test")
  val col = db.getCollection("sample")

  val docs = Range(0, 5).map { n => Document("name" -> s"item$n", "value" -> (n * 2)) }

  val res = Await.result(col.insertMany(docs).toFuture(), 5 seconds)

  println(res)

  println("-----")

  Await.result(col.find().toFuture(), 5 seconds).foreach(println)
}
