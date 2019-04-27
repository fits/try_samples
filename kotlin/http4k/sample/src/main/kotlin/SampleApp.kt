
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main() {
    val app: HttpHandler = { req ->
        Response(Status.OK).body("receive-${req.query("v")}")
    }

    val server = app.asServer(Jetty(8080)).start()

    println("server started ...")

    Runtime.getRuntime().addShutdownHook(Thread {
        println("server stop")
        server.stop()
    })
}
