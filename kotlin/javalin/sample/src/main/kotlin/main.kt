
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*

data class Item(val id: String, val value: Int)

fun main() {
    val app = Javalin.create().start(8080)

    app.get("/") {
        it.result("sample data")
    }

    app.routes {
        get("/items") { ctx ->
            ctx.json(
                listOf(
                    Item("item-1", 1),
                    Item("item-2", 2),
                    Item("item-3", 3),
                )
            )
        }

        post("/items") { ctx ->
            val item = ctx.body<Item>()

            println("post item: ${item}")

            ctx.status(201)
        }
    }
}