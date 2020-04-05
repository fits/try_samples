
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.model.rest.RestBindingMode
import java.util.UUID

typealias StockId = String

data class CreateStock(val initialQty: Int = 0)
data class UpdateStock(val qty: Int = 0)

data class Stock(val id: StockId, val qty: Int)

fun main() {
    val store = mutableMapOf<StockId, Stock>()

    val ctx = DefaultCamelContext()

    ctx.addRoutes(
        object : RouteBuilder() {
            override fun configure() {
                restConfiguration()
                        .component("undertow")
                        .port(8080)
                        .bindingMode(RestBindingMode.json)

                rest("/stocks")
                        .post().type(CreateStock::class.java)
                            .route().process {
                                val qty = it.message.getBody(CreateStock::class.java).initialQty

                                if (qty >= 0) {
                                    val stock = Stock(UUID.randomUUID().toString(), qty)

                                    store.putIfAbsent(stock.id, stock) ?: run {
                                        it.message.body = stock
                                    }
                                }
                            }.endRest()
                        .put("/{id}").type(UpdateStock::class.java)
                            .route().process {
                                val qty = it.message.getBody(UpdateStock::class.java).qty

                                store[it.message.headers["id"]]?.let { stock ->
                                    if (qty >= 0) {
                                        val newStock = stock.copy(qty = qty)

                                        store[stock.id] = newStock
                                        it.message.body = newStock
                                    }
                                }
                            }.endRest()
                        .delete("/{id}")
                            .route().process {
                                store.remove(it.message.headers["id"])
                            }.endRest()
                        .get().route().setBody { store.values }.endRest()
                        .get("/{id}").route().setBody { store[it.message.headers["id"]] }.endRest()
            }
        }
    )

    ctx.start()

    Runtime.getRuntime().addShutdownHook(
            Thread {
                println("shutdown")
                ctx.stop()
            }
    )
}
