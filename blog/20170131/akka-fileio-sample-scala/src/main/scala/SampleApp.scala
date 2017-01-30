import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString

import java.nio.file.Paths
import java.io.IOException

object SampleApp extends App {
  implicit val system = ActorSystem()
  import system.dispatcher // implicit ExecutionContext
  implicit val materializer = ActorMaterializer()

  val res1 = Source.single("sample data")
                   .map(ByteString.fromString)
                   .runWith(FileIO.toPath(Paths.get("sample1.txt")))

  val res2 = FileIO.fromPath(Paths.get("sample2.txt"))
                   .map(_.utf8String)
                   .recover {
                     case e: IOException => s"invalid file, ${e}"
                   }
                   .runForeach(println)

  res1.flatMap(_ => res2).onComplete(_ => system.terminate)
}