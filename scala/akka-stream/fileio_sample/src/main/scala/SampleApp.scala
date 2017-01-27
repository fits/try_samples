import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString

object SampleApp extends App {
  implicit val system = ActorSystem()
  import system.dispatcher
  implicit val materializer = ActorMaterializer()

  val res1 = Source.single("sample data")
                   .map(ByteString.fromString)
                   .runWith(FileIO.toPath(Paths.get("sample1.txt")))

  val res2 = FileIO.fromPath(Paths.get("sample2.txt"))
                   .map(_.utf8String)
                   .recover {
                     case e => "no file"
                   }
                   .runForeach(println)

  res1.flatMap(_ => res2).onComplete(_ => system.terminate)
}