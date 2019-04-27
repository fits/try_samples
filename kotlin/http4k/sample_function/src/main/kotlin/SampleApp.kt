
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

fun main() {
    val app: HttpHandler = { req ->
        Response(Status.OK).body("receive-${req.query("v")}")
    }

    val req = Request(Method.GET, "/").query("v", "abc")

    println(app(req))
}