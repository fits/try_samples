package sample.app

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.dynamodb.impl.DynamoSettings
import akka.stream.alpakka.dynamodb.scaladsl.DynamoClient
import akka.stream.alpakka.dynamodb.scaladsl.DynamoImplicits._
import akka.stream.scaladsl.Source
import com.amazonaws.services.dynamodbv2.model._

import scala.concurrent.Await
import scala.concurrent.duration._

object CreateTableApp extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val settings = DynamoSettings(system)
  val client = DynamoClient(settings)

  val table = new CreateTableRequest()
    .withTableName("Sample1")
    .withKeySchema(
      new KeySchemaElement()
        .withAttributeName("Id")
        .withKeyType(KeyType.HASH)
    )
    .withAttributeDefinitions(
      new AttributeDefinition()
        .withAttributeName("Id")
        .withAttributeType(ScalarAttributeType.S)
    )
    .withProvisionedThroughput(
      new ProvisionedThroughput()
        .withReadCapacityUnits(1L)
        .withWriteCapacityUnits(1L)
    )

  val create = Source.single(table.toOp).via(client.flow)

  Await.result(create.runForeach(println), 5 seconds)

  val listup = client.single(new ListTablesRequest())

  Await.result(listup, 5 seconds).getTableNames().forEach(println)

  system.terminate()
}
