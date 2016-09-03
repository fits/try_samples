
import akka.actor.{ActorSystem, Props}
import akka.pattern._
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.stream.ActorMaterializer
import sample._

import scala.language.postfixOps
import scala.concurrent.duration._

object SampleApp extends App {
  implicit val system = ActorSystem("sample1")
  implicit val exeContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val actor = system.actorOf(Props[SampleActor])

  val readJournal = PersistenceQuery(system)
    .readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)

  readJournal.eventsByTag("sampletag").runForeach { env =>
    println(s"### eventbytag id: ${env.persistenceId}, offset: ${env.offset}, event: ${env.event}")
  }

  actor ! "dump"

  (1 to 5) foreach { i =>
    actor ! CounterAdd(i)
  }

  actor ! "dump"

  gracefulStop(actor, 5 seconds, "end").onComplete(_ => system.terminate)
}
