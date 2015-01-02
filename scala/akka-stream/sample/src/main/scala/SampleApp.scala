
import akka.actor.ActorSystem
import akka.stream.FlowMaterializer
import akka.stream.scaladsl.Source

object SampleApp extends App {
	implicit val system = ActorSystem("sample")
	
	import system.dispatcher
	
	implicit val materializer = FlowMaterializer()

	Source(() => "test sample 123".split("\\s").iterator)
		.map(_.toUpperCase)
		.foreach(println)
		.onComplete(_ => system.shutdown())
	
}