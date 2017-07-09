
import com.google.gson.Gson
import org.jetbrains.ktor.host.*
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.*
import org.jetbrains.ktor.request.receiveText
import org.jetbrains.ktor.response.*
import org.jetbrains.ktor.routing.*

data class Data(val a: Map<String, *>, val b: Map<String, *>)

fun main(args: Array<String>) {
    val gson = Gson()

    embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                call.respondText("sample")
            }
            get("/{start}/{end}") {
                val start = call.parameters["start"]
                val end = call.parameters["end"]

                println("${start} -> ${end}")

                call.respondText("ok")
            }
            post("/") {
                val d = gson.fromJson(
                        call.receiveText(),
                        Map::class.java
                )

                println(d)

                call.respondText("true", ContentType.Application.Json)
            }
            post("/data") {
                val d = gson.fromJson(
                        call.receiveText(),
                        Data::class.java
                )

                println(d)

                call.respondText("true", ContentType.Application.Json)
            }
        }
    }.start(wait = true)
}
