
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

data class Item(val id: String, val value: Int)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        route("/items") {
            get("{id}") {
                call.parameters["id"]?.let {
                    call.respond(Item(it, 1))
                } ?: call.respond(HttpStatusCode.OK)
            }
            post {
                val item = call.receive<Item>()
                println(item)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

