package sample.stockmove.model.b

import java.time.OffsetDateTime
import java.util.UUID
import kotlin.test.assertNull

typealias LocationCode = String
typealias ItemCode = String

typealias Quantity = Int
typealias Revision = Int

typealias EventId = String
typealias EventDate = OffsetDateTime
typealias SeqNo = Long

typealias StockMoveId = String

interface Event {
    val eventId: EventId
    val date: EventDate
}

data class Stock(val item: ItemCode, val location: LocationCode, val qty: Quantity = 0,
                 val assignedQty: Quantity = 0, val availableQty: Quantity = qty - assignedQty) {
    internal fun applyEvent(event: StockMoveEvent): Stock =
        when (event) {
            is StockMoveEvent.Assigned -> applyEvent(event)
            is StockMoveEvent.AssignShipped -> applyEvent(event)
            is StockMoveEvent.Shipped -> applyEvent(event)
            is StockMoveEvent.AssignShipFailed -> applyEvent(event)
            is StockMoveEvent.Arrived -> applyEvent(event)
            else -> this
    }

    private fun applyEvent(event: StockMoveEvent.Assigned): Stock =
        if (event.item == item && event.location == location)
            copy(
                assignedQty = assignedQty + event.assignedQty,
                availableQty = availableQty - event.assignedQty
            )
        else this

    private fun applyEvent(event: StockMoveEvent.AssignShipped): Stock =
        if (event.item == item && event.location == location)
            copy(
                qty = qty - event.shippedQty,
                assignedQty = assignedQty - event.assignedQty,
                availableQty = availableQty - (event.shippedQty - event.assignedQty)
            )
        else this

    private fun applyEvent(event: StockMoveEvent.Shipped): Stock =
        if (event.item == item && event.location == location)
            copy(
                qty = qty - event.shippedQty,
                availableQty = availableQty - event.shippedQty
            )
        else this

    private fun applyEvent(event: StockMoveEvent.AssignShipFailed): Stock =
        if (event.item == item && event.location == location)
            copy(
                assignedQty = assignedQty - event.assignedQty,
                availableQty = availableQty + event.assignedQty
            )
        else this

    private fun applyEvent(event: StockMoveEvent.Arrived): Stock =
        if (event.item == item && event.location == location)
            copy(
                qty = qty + event.arrivedQty,
                availableQty = availableQty + event.arrivedQty
            )
        else this
}

data class StockMoveOrder(val item: ItemCode = "", val qty: Quantity = 0,
                          val from: LocationCode = "", val to: LocationCode = "")

interface StockMoveInfo {
    val moveId: StockMoveId
    val order: StockMoveOrder
    val revision: Revision
}

interface Assigning {
    val assignedQty: Quantity
}

sealed class StockMove : StockMoveInfo {
    object Empty : StockMove() {
        override val moveId: StockMoveId = ""
        override val order: StockMoveOrder = StockMoveOrder()
        override val revision: Revision = 0

        internal fun applyEvent(event: StockMoveEvent.Opened): StockMove =
            Opened(event.moveId, event.order, revision + 1)
    }

    data class Opened(override val moveId: StockMoveId,
                      override val order: StockMoveOrder,
                      override val revision: Revision) : StockMove() {

        internal fun applyEvent(event: StockMoveEvent.Assigned): StockMove =
            Assigned(moveId, order, event.assignedQty,revision + 1)

        internal fun applyEvent(event: StockMoveEvent.AssignFailed): StockMove =
            AssignFailed(moveId, order, event.assignFailedQty,revision + 1)

        internal fun applyEvent(event: StockMoveEvent.Shipped): StockMove =
            Shipped(moveId, order, event.shippedQty, revision + 1)

        internal fun applyEvent(event: StockMoveEvent.ShipFailed): StockMove =
            ShipFailed(moveId, order, revision + 1)

        internal fun applyEvent(event: StockMoveEvent.Arrived): StockMove =
            Arrived(moveId, order, 0, event.arrivedQty, revision + 1)

        internal fun applyEvent(event: StockMoveEvent.Cancelled): StockMove =
            Cancelled(moveId, order, revision + 1)
    }

    data class Assigned(override val moveId: StockMoveId,
                        override val order: StockMoveOrder,
                        override val assignedQty: Quantity,
                        override val revision: Revision) : StockMove(), Assigning {
        internal fun applyEvent(event: StockMoveEvent.AssignShipped): StockMove =
            Shipped(moveId, order, event.shippedQty, revision + 1)

        internal fun applyEvent(event: StockMoveEvent.AssignShipFailed): StockMove =
            ShipFailed(moveId, order, revision + 1)
    }

    data class AssignFailed(override val moveId: StockMoveId,
                            override val order: StockMoveOrder,
                            val assignFailedQty: Quantity,
                            override val revision: Revision) : StockMove()

    data class Shipped(override val moveId: StockMoveId,
                       override val order: StockMoveOrder,
                       val shippedQty: Quantity,
                       override val revision: Revision) : StockMove() {
        internal fun applyEvent(event: StockMoveEvent.Arrived): StockMove =
            Arrived(moveId, order, shippedQty, event.arrivedQty, revision + 1)
    }

    data class ShipFailed(override val moveId: StockMoveId,
                          override val order: StockMoveOrder,
                          override val revision: Revision) : StockMove()

    data class Arrived(override val moveId: StockMoveId,
                       override val order: StockMoveOrder,
                       val shippedQty: Quantity,
                       val arrivedQty: Quantity,
                       override val revision: Revision) : StockMove()

    data class Cancelled(override val moveId: StockMoveId,
                         override val order: StockMoveOrder,
                         override val revision: Revision) : StockMove()

    companion object {
        fun applyEvent(state: StockMove, event: StockMoveEvent): StockMove =
            if (state != Empty && state.moveId != event.moveId)
                state
            else
                when (state) {
                    is Empty -> when (event) {
                        is StockMoveEvent.Opened -> state.applyEvent(event)
                        else -> state
                    }
                    is Opened -> when (event) {
                        is StockMoveEvent.Assigned -> state.applyEvent(event)
                        is StockMoveEvent.AssignFailed -> state.applyEvent(event)
                        is StockMoveEvent.Shipped -> state.applyEvent(event)
                        is StockMoveEvent.ShipFailed -> state.applyEvent(event)
                        is StockMoveEvent.Arrived -> state.applyEvent(event)
                        is StockMoveEvent.Cancelled -> state.applyEvent(event)
                        else -> state
                    }
                    is Assigned -> when (event) {
                        is StockMoveEvent.AssignShipped -> state.applyEvent(event)
                        is StockMoveEvent.AssignShipFailed -> state.applyEvent(event)
                        else -> state
                    }
                    is Shipped -> when (event) {
                        is StockMoveEvent.Arrived -> state.applyEvent(event)
                        else -> state
                    }
                    is AssignFailed,
                    is ShipFailed,
                    is Arrived,
                    is Cancelled -> state
                }
    }
}

interface StockMoveEventInfo {
    val moveId: StockMoveId
}

sealed class StockMoveEvent : Event, StockMoveEventInfo {
    data class Opened(
        override val moveId: StockMoveId,
        val order: StockMoveOrder,
        override val date: EventDate = EventDate.now(),
        override val eventId: EventId = createEventId()) : StockMoveEvent()

    data class Assigned(
        override val moveId: StockMoveId,
        val item: ItemCode,
        val location: LocationCode,
        val assignedQty: Quantity,
        override val date: EventDate = EventDate.now(),
        override val eventId: EventId = createEventId()) : StockMoveEvent()

    data class AssignFailed(
        override val moveId: StockMoveId,
        val item: ItemCode,
        val location: LocationCode,
        val assignFailedQty: Quantity,
        override val date: EventDate = EventDate.now(),
        override val eventId: EventId = createEventId()) : StockMoveEvent()

    data class AssignShipped(
        override val moveId: StockMoveId,
        val item: ItemCode,
        val location: LocationCode,
        val assignedQty: Quantity,
        val shippedQty: Quantity,
        override val date: EventDate = EventDate.now(),
        override val eventId: EventId = createEventId()) : StockMoveEvent()

    data class AssignShipFailed(
        override val moveId: StockMoveId,
        val item: ItemCode,
        val location: LocationCode,
        val assignedQty: Quantity,
        override val date: EventDate = EventDate.now(),
        override val eventId: EventId = createEventId()) : StockMoveEvent()

    data class Shipped(
        override val moveId: StockMoveId,
        val item: ItemCode,
        val location: LocationCode,
        val shippedQty: Quantity,
        override val date: EventDate = EventDate.now(),
        override val eventId: EventId = createEventId()) : StockMoveEvent()

    data class ShipFailed(
        override val moveId: StockMoveId,
        val item: ItemCode,
        val location: LocationCode,
        override val date: EventDate = EventDate.now(),
        override val eventId: EventId = createEventId()) : StockMoveEvent()

    data class Arrived(
        override val moveId: StockMoveId,
        val item: ItemCode,
        val location: LocationCode,
        val arrivedQty: Quantity,
        override val date: EventDate = EventDate.now(),
        override val eventId: EventId = createEventId()) : StockMoveEvent()

    data class Cancelled(
        override val moveId: StockMoveId,
        override val date: EventDate = EventDate.now(),
        override val eventId: EventId = createEventId()) : StockMoveEvent()

    companion object {
        fun createEventId(): EventId = "stock-move-event:${UUID.randomUUID()}"
    }
}

interface StockMoveCommandInfo {
    val moveId: StockMoveId
}

sealed class StockMoveCommand : StockMoveCommandInfo {
    data class Open(override val moveId: StockMoveId, val order: StockMoveOrder) : StockMoveCommand()
    data class Assign(override val moveId: StockMoveId) : StockMoveCommand()
    data class Cancel(override val moveId: StockMoveId) : StockMoveCommand()
}

sealed class StockMoveReport : StockMoveCommandInfo {
    data class Ship(override val moveId: StockMoveId, val shippedQty: Quantity) : StockMoveReport()
    data class ShipFail(override val moveId: StockMoveId) : StockMoveReport()
    data class Arrive(override val moveId: StockMoveId, val arrivedQty: Quantity) : StockMoveReport()
}

typealias StoredEvent<E> = Pair<SeqNo, E>

interface EventStore<K, E> {
    fun allEvents(skipSeqNo: SeqNo = 0): List<StoredEvent<E>>
    fun events(key: K, skipSeqNo: SeqNo = 0): List<StoredEvent<E>>
    fun storeEvent(key: K, event: E): SeqNo?
}

class MemoryEventStore<K, E>(val getId: (E) -> K) : EventStore<K, E> {
    private val store = mutableListOf<Pair<SeqNo, E>>()

    override fun allEvents(skipSeqNo: SeqNo): List<StoredEvent<E>> = search(skipSeqNo)

    override fun events(key: K, skipSeqNo: SeqNo): List<StoredEvent<E>> =
        search(skipSeqNo) {
            getId(it) == key
        }

    override fun storeEvent(key: K, event: E): SeqNo? =
        store.add(Pair(store.size + 1L, event)).takeIf { it }?.let {
            store.last().first
        }

    private fun search(skipSeqNo: SeqNo = 0, predicate: (E) -> Boolean = { true }): List<StoredEvent<E>> =
        store.filter {
            it.first > skipSeqNo && predicate(it.second)
        }
}

interface StockMoveService {
    fun command(cmd: StockMoveCommand): StockMove?
    fun report(report: StockMoveReport): StockMove?
    fun status(moveId: StockMoveId): StockMove?

    fun stock(item: ItemCode, location: LocationCode): Stock
}

class SimpleStockMoveService(
    private val eventStore: EventStore<StockMoveId, StockMoveEvent>
) : StockMoveService {
    override fun command(cmd: StockMoveCommand): StockMove? =
        when (cmd) {
            is StockMoveCommand.Open -> updateState(cmd.moveId) { toEvent(cmd, it) }
            is StockMoveCommand.Assign -> updateState(cmd.moveId) { toEvent(cmd, it) }
            is StockMoveCommand.Cancel -> updateState(cmd.moveId) { StockMoveEvent.Cancelled(cmd.moveId) }
        }

    override fun report(rep: StockMoveReport): StockMove? =
        when (rep) {
            is StockMoveReport.Ship -> updateState(rep.moveId) { toEvent(rep, it) }
            is StockMoveReport.ShipFail -> updateState(rep.moveId) { toEvent(rep, it) }
            is StockMoveReport.Arrive -> updateState(rep.moveId) { toEvent(rep, it) }
        }

    override fun status(moveId: StockMoveId): StockMove? =
        restoreState(moveId).takeIf { it != StockMove.Empty }

    override fun stock(item: ItemCode, location: LocationCode): Stock =
        eventStore.allEvents().map {
            it.second
        }.fold(Stock(item, location), Stock::applyEvent)

    private fun restoreState(moveId: StockMoveId): StockMove =
        eventStore.events(moveId).map {
            it.second
        }.fold(StockMove.Empty, StockMove.Companion::applyEvent)

    private fun toEvent(cmd: StockMoveCommand.Open, state: StockMove): StockMoveEvent? =
        if (state == StockMove.Empty &&
            cmd.moveId.isNotBlank() &&
            cmd.order.item.isNotBlank() &&
            cmd.order.from.isNotBlank() &&
            cmd.order.to.isNotBlank() &&
            cmd.order.qty > 0)
            StockMoveEvent.Opened(cmd.moveId, cmd.order)
        else null

    private fun toEvent(cmd: StockMoveCommand.Assign, state: StockMove): StockMoveEvent? {
        val order = state.order
        val stock = stock(order.item, order.from)

        val event =
            if (order.qty <= stock.availableQty)
                StockMoveEvent.Assigned(state.moveId, order.item, order.from, order.qty)
            else
                StockMoveEvent.AssignFailed(state.moveId, order.item, order.from, order.qty)

        return event
    }

    private fun toEvent(rep: StockMoveReport.Ship, state: StockMove): StockMoveEvent? =
        if (rep.shippedQty > 0)
            if (state is Assigning)
                StockMoveEvent.AssignShipped(state.moveId, state.order.item,
                    state.order.from, state.assignedQty, rep.shippedQty)
            else
                StockMoveEvent.Shipped(state.moveId, state.order.item,
                    state.order.from, rep.shippedQty)
        else null

    private fun toEvent(rep: StockMoveReport.ShipFail, state: StockMove): StockMoveEvent? =
        if (state is Assigning)
            StockMoveEvent.AssignShipFailed(state.moveId, state.order.item,
                state.order.from, state.assignedQty)
        else
            StockMoveEvent.ShipFailed(state.moveId, state.order.item,
                state.order.from)

    private fun toEvent(rep: StockMoveReport.Arrive, state: StockMove): StockMoveEvent? =
        if (rep.arrivedQty >= 0)
            StockMoveEvent.Arrived(rep.moveId, state.order.item, state.order.to, rep.arrivedQty)
        else null

    private fun updateState(moveId: StockMoveId, toEvent: (StockMove) -> StockMoveEvent?): StockMove? =
        restoreState(moveId).let { state ->
            toEvent(state)?.let { event ->
                StockMove.applyEvent(state, event).takeIf {
                    it != state
                }?.let { newState ->
                    eventStore.storeEvent(newState.moveId, event)?.let {
                        newState
                    }
                }
            }
        }
}

fun main() {
    val eventStore = MemoryEventStore(StockMoveEvent::moveId)

    val service = SimpleStockMoveService(eventStore)

    val item1 = "item-1"
    val loc0 = "adjustment-0"
    val loc1 = "location-1"
    val loc2 = "location-2"
    val loc3 = "customer-a"

    service.command(StockMoveCommand.Open("mv1", StockMoveOrder(item1, 30, loc0, loc1)))?.let {
        println(it)

        println( service.report(StockMoveReport.Arrive(it.moveId, it.order.qty)) )

        println( service.stock(item1, loc0) )
        println( service.stock(item1, loc1) )
    }

    println("----------------")

    service.command(StockMoveCommand.Open("mv2", StockMoveOrder(item1, 2, loc1, loc2)))?.let {
        println(it)

        println( service.command(StockMoveCommand.Assign(it.moveId)) )

        assertNull( service.command(StockMoveCommand.Cancel(it.moveId)) )

        println( service.stock(item1, loc1) )

        println( service.report(StockMoveReport.Ship(it.moveId, it.order.qty)) )

        println( service.stock(item1, loc1) )
        println( service.stock(item1, loc2) )

        println( service.report(StockMoveReport.Arrive(it.moveId, it.order.qty)) )

        println(service.stock(item1, loc1))
        println(service.stock(item1, loc2))
    }

    println("----------------")

    service.command(StockMoveCommand.Open("mv3", StockMoveOrder(item1, 1, loc1, loc3)))?.let {
        println(it)

        println( service.report(StockMoveReport.Ship(it.moveId, it.order.qty)) )

        println( service.stock(item1, loc1) )
        println( service.stock(item1, loc3) )

        println( service.report(StockMoveReport.Arrive(it.moveId, it.order.qty)) )

        println(service.stock(item1, loc1))
        println(service.stock(item1, loc3))
    }

    println("----------------")

    service.command(StockMoveCommand.Open("mv4", StockMoveOrder(item1, 3, loc2, loc3)))?.let {
        println(it)

        println( service.command(StockMoveCommand.Assign(it.moveId)) )

        println( service.stock(item1, loc2) )
        println( service.stock(item1, loc3) )
    }

    println("----------------")

    service.command(StockMoveCommand.Open("mv5", StockMoveOrder(item1, 1, loc2, loc3)))?.let {
        println(it)

        println( service.command(StockMoveCommand.Assign(it.moveId)) )

        println( service.stock(item1, loc2) )
        println( service.stock(item1, loc3) )

        println( service.report(StockMoveReport.ShipFail(it.moveId)) )

        println( service.stock(item1, loc2) )
        println( service.stock(item1, loc3) )
    }

    println("----------------")

    service.command(StockMoveCommand.Open("mv6", StockMoveOrder(item1, 1, loc2, loc3)))?.let {
        assertNull( service.report(StockMoveReport.Ship(it.moveId, 0)) )
    }

    println("----------------")

    service.command(StockMoveCommand.Open("mv7", StockMoveOrder(item1, 1, loc2, loc3)))?.let {
        println(it)

        assertNull( service.report(StockMoveReport.Arrive(it.moveId, -1)) )

        println( service.stock(item1, loc2) )
        println( service.stock(item1, loc3) )

        println( service.report(StockMoveReport.Arrive(it.moveId, 0)) )

        println( service.stock(item1, loc2) )
        println( service.stock(item1, loc3) )
    }
}