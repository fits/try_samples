
import akka.actor.ActorSystem
import akka.http.javadsl.ConnectHttp
import akka.http.javadsl.Http
import akka.http.javadsl.ServerBinding
import akka.http.javadsl.server.AllDirectives
import akka.http.javadsl.server.Route
import akka.stream.ActorMaterializer

fun main(args: Array<String>) {
    val system = ActorSystem.apply("sample")

    val http = Http.get(system)
    val materializer = ActorMaterializer.create(system)

    val routeFlow = SampleRoute.createRoute().flow(system, materializer)

    val binding = http.bindAndHandle(
            routeFlow,
            ConnectHttp.toHost("localhost", 8080),
            materializer
    )

    println("start ...")

    readLine()

    binding.thenCompose(ServerBinding::unbind).thenAccept {
        system.terminate()
    }
}

object SampleRoute : AllDirectives() {
    fun createRoute(): Route = route(
            path("sample") {
                get {
                    complete("sample")
                }
            }
    )
}

