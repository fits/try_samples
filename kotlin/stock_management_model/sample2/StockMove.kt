import java.util.*

typealias ItemCode = String
typealias LocationCode = String
typealias StockId = Pair<ItemCode, LocationCode>
typealias MoveId = String
typealias Quantity = Int

data class Stock(
    val stockId: StockId,
    val qty: Quantity = 0,
    val assignedQty: Quantity = 0,
    val availableQty: Quantity = qty - assignedQty
)

enum class StockMoveStatus {
    Empty,
    Created,
    Assigned,
    AssignFailed,
    Shipped,
    Arrived,
}

data class StockMove(
    val moveId: MoveId = "",
    val item: ItemCode = "",
    val qty: Quantity = 0,
    val from: LocationCode = "",
    val to: LocationCode = "",
    val status: StockMoveStatus = StockMoveStatus.Empty,
    val assignedQty: Quantity = 0,
    val shippedQty: Quantity = 0,
    val arrivedQty: Quantity = 0
)

interface StockMoveEventInfo {
    val moveId: MoveId
}

sealed class StockMoveEvent : StockMoveEventInfo {
    data class Created(override val moveId: MoveId,
                       val item: ItemCode, val qty: Quantity,
                       val from: LocationCode, val to: LocationCode): StockMoveEvent()

    data class Assigned(override val moveId: MoveId, val item: ItemCode, val from: LocationCode,
                        val assignedQty: Quantity) : StockMoveEvent()

    data class AssignFailed(override val moveId: MoveId, val item: ItemCode, val from: LocationCode,
                        val assignFailedQty: Quantity) : StockMoveEvent()

    data class AssignShipped(override val moveId: MoveId, val item: ItemCode, val from: LocationCode,
                             val assignedQty: Quantity, val shippedQty: Quantity) : StockMoveEvent()

    data class Shipped(override val moveId: MoveId, val item: ItemCode, val from: LocationCode,
                       val shippedQty: Quantity) : StockMoveEvent()

    data class Arrived(override val moveId: MoveId, val item: ItemCode, val to: LocationCode,
                       val arrivedQty: Quantity) : StockMoveEvent()
}

interface StockMoveService {
    fun request(item: ItemCode, qty: Quantity, from: LocationCode, to: LocationCode): MoveId?

    fun assign(moveId: MoveId): StockMove?
    fun shipped(moveId: MoveId, shippedQty: Quantity): StockMove?
    fun arrived(moveId: MoveId, arrivedQty: Quantity): StockMove?

    fun status(moveId: MoveId): StockMove?
}

class SampleStockMoveService : StockMoveService {
    val eventStore = mutableListOf<StockMoveEvent>()

    override fun request(item: ItemCode, qty: Quantity, from: LocationCode, to: LocationCode): MoveId? {
        if (item.isBlank() || qty < 1 || from.isBlank() || to.isBlank()) {
            return null
        }

        val event = StockMoveEvent.Created(moveId(), item, qty, from, to)

        return storeEvent(event) {
            it.moveId
        }
    }

    override fun assign(moveId: MoveId): StockMove? =
        status(moveId)?.takeIf {
            it.status == StockMoveStatus.Created
        }?.let { mv ->
            val stock = restoreStock(mv.item, mv.from)

            val event =
                if (stock.availableQty >= mv.qty)
                    StockMoveEvent.Assigned(mv.moveId, mv.item, mv.from, mv.qty)
                else
                    StockMoveEvent.AssignFailed(mv.moveId, mv.item, mv.from, mv.qty)

            storeEvent(event) {
                applyEvent(mv, it)
            }
        }

    override fun shipped(moveId: MoveId, shippedQty: Quantity): StockMove? =
        status(moveId)?.takeIf {
            listOf(StockMoveStatus.Assigned, StockMoveStatus.Created).contains(it.status) &&
                    shippedQty > 0
        }?.let { mv ->
            val event =
                if (mv.status == StockMoveStatus.Assigned)
                    StockMoveEvent.AssignShipped(mv.moveId, mv.item, mv.from,
                            mv.assignedQty, shippedQty)
                else
                    StockMoveEvent.Shipped(mv.moveId, mv.item, mv.from, shippedQty)

            storeEvent(event) {
                applyEvent(mv, it)
            }
        }

    override fun arrived(moveId: MoveId, arrivedQty: Quantity): StockMove? =
        status(moveId)?.takeIf {
            listOf(StockMoveStatus.Shipped).contains(it.status) &&
                    arrivedQty >= 0
        }?.let { mv ->
            val event = StockMoveEvent.Arrived(mv.moveId, mv.item, mv.to, arrivedQty)

            storeEvent(event) {
                applyEvent(mv, it)
            }
        }

    override fun status(moveId: MoveId): StockMove? =
        eventStore.filter {
            it.moveId == moveId
        }.fold(StockMove(), ::applyEvent).takeIf {
            it.moveId == moveId
        }

    private fun findEventsForStock(item: ItemCode, locationCode: LocationCode) =
        eventStore.filter {
            when (it) {
                is StockMoveEvent.Assigned -> it.item == item && it.from == locationCode
                is StockMoveEvent.AssignShipped -> it.item == item && it.from == locationCode
                is StockMoveEvent.Shipped -> it.item == item && it.from == locationCode
                is StockMoveEvent.Arrived -> it.item == item && it.to == locationCode
                else -> false
            }
        }

    fun restoreStock(item: ItemCode, locationCode: LocationCode): Stock =
        findEventsForStock(item, locationCode).fold(Stock(Pair(item, locationCode))) { acc, event ->
            when (event) {
                is StockMoveEvent.Assigned -> acc.copy(
                    assignedQty = acc.assignedQty + event.assignedQty,
                    availableQty = acc.availableQty - event.assignedQty
                )
                is StockMoveEvent.AssignShipped -> acc.copy(
                    qty = acc.qty - event.shippedQty,
                    assignedQty = acc.assignedQty - event.assignedQty,
                    availableQty = acc.availableQty - (event.shippedQty - event.assignedQty)
                )
                is StockMoveEvent.Shipped -> acc.copy(
                    qty = acc.qty - event.shippedQty,
                    availableQty = acc.availableQty - event.shippedQty
                )
                is StockMoveEvent.Arrived -> acc.copy(
                    qty = acc.qty + event.arrivedQty,
                    availableQty = acc.availableQty + event.arrivedQty
                )
                else -> acc
            }
        }

    private fun applyEvent(move: StockMove, event: StockMoveEvent): StockMove =
        when (event) {
            is StockMoveEvent.Created -> StockMove(event.moveId, event.item, event.qty,
                    event.from, event.to, StockMoveStatus.Created)
            is StockMoveEvent.Assigned -> move.copy(status = StockMoveStatus.Assigned,
                    assignedQty = event.assignedQty)
            is StockMoveEvent.AssignFailed -> move.copy(status = StockMoveStatus.AssignFailed)
            is StockMoveEvent.AssignShipped -> move.copy(status = StockMoveStatus.Shipped,
                    assignedQty = move.assignedQty - event.assignedQty, shippedQty = event.shippedQty)
            is StockMoveEvent.Shipped -> move.copy(status = StockMoveStatus.Shipped,
                    shippedQty = event.shippedQty)
            is StockMoveEvent.Arrived -> move.copy(status = StockMoveStatus.Arrived,
                    arrivedQty = event.arrivedQty)
        }

    private fun <R> storeEvent(event: StockMoveEvent, handle: (StockMoveEvent) -> R): R? =
        eventStore.add(event).takeIf { it }?.let {
            handle(event)
        }

    private fun moveId(): MoveId = "move:${UUID.randomUUID()}"
}

fun main() {
    val item1 = "item-1"
    val loc1 = "inventory-1"
    val loc2 = "inventory-2"
    val loc3 = "customer-1"

    val service = SampleStockMoveService()
    service.eventStore.add(StockMoveEvent.Arrived("dummy1", item1, loc1, 10))
    service.eventStore.add(StockMoveEvent.Arrived("dummy1", item1, loc2, 30))

    println(service.restoreStock(item1, loc1))
    println(service.restoreStock(item1, loc2))

    service.eventStore.add(StockMoveEvent.Assigned("dummy1", item1, loc1, 2))

    println(service.restoreStock(item1, loc1))

    service.eventStore.add(StockMoveEvent.AssignShipped("dummy1", item1, loc1, 2, 2))

    println(service.restoreStock(item1, loc1))

    println("-----")

    val mv1 = service.request(item1, 2, loc1, loc2)

    mv1?.let {
        println(service.status(it))

        println(service.assign(it))

        println(service.restoreStock(item1, loc1))

        println(service.shipped(it, 2))

        println(service.restoreStock(item1, loc1))

        println(service.arrived(it, 2))

        println(service.restoreStock(item1, loc2))
    }

    println("-----")

    val mv2 = service.request(item1, 1, loc2, loc3)

    mv2?.let {
        println(service.status(it))

        println(service.shipped(it, 1))

        println(service.restoreStock(item1, loc2))

        println(service.arrived(it, 1))

        println(service.restoreStock(item1, loc3))
    }
}