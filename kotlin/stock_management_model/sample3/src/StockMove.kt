package sample.stockmove.model

import java.time.OffsetDateTime
import java.util.UUID

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
                 val assignedQty: Quantity = 0, val availableQty: Quantity = qty - assignedQty)

data class StockMoveOrder(val item: ItemCode, val qty: Quantity,
                          val from: LocationCode, val to: LocationCode)

interface StockMoveIdentity {
    val moveId: StockMoveId
    val order: StockMoveOrder
    val revision: Revision
}

interface Assigning {
    val assignedQty: Quantity
}

sealed class StockMove : StockMoveIdentity {
    object EmptyStockMove : StockMove() {
        override val moveId: StockMoveId = ""
        override val order: StockMoveOrder = StockMoveOrder("", 0, "", "")
        override val revision: Revision = 0
    }

    data class OpenedStockMove(override val moveId: StockMoveId,
                               override val order: StockMoveOrder,
                               override val revision: Revision) : StockMove()

    data class AssignedStockMove(override val moveId: StockMoveId,
                                 override val order: StockMoveOrder,
                                 override val assignedQty: Quantity,
                                 override val revision: Revision) : StockMove(), Assigning

    data class AssignFailedStockMove(override val moveId: StockMoveId,
                                     override val order: StockMoveOrder,
                                     val assignFailedQty: Quantity,
                                     override val revision: Revision) : StockMove()

    data class ShippedStockMove(override val moveId: StockMoveId,
                                override val order: StockMoveOrder,
                                val shippedQty: Quantity,
                                override val revision: Revision) : StockMove()

    data class ShipFailedStockMove(override val moveId: StockMoveId,
                                   override val order: StockMoveOrder,
                                   override val revision: Revision) : StockMove()

    data class ArrivedStockMove(override val moveId: StockMoveId,
                                override val order: StockMoveOrder,
                                val shippedQty: Quantity,
                                val arrivedQty: Quantity,
                                override val revision: Revision) : StockMove()

    data class CancelledStockMove(override val moveId: StockMoveId,
                                  override val order: StockMoveOrder,
                                  override val revision: Revision) : StockMove()

    companion object {
        fun applyEvent(state: StockMove, event: StockMoveEvent): StockMove =
            if (state != EmptyStockMove && state.moveId != event.moveId)
                state
            else
                when (state) {
                    is EmptyStockMove -> when (event) {
                        is StockMoveEvent.Opened ->
                            OpenedStockMove(event.moveId, event.order, state.revision + 1)
                        else -> state
                    }
                    is OpenedStockMove -> when (event) {
                        is StockMoveEvent.Assigned ->
                            AssignedStockMove(state.moveId, state.order,
                                event.assignedQty,state.revision + 1)
                        is StockMoveEvent.AssignFailed ->
                            AssignFailedStockMove(state.moveId, state.order,
                                event.assignFailedQty,state.revision + 1)
                        is StockMoveEvent.Shipped ->
                            ShippedStockMove(state.moveId, state.order,
                                event.shippedQty, state.revision + 1)
                        is StockMoveEvent.Arrived ->
                            ArrivedStockMove(state.moveId, state.order,
                                0, event.arrivedQty, state.revision + 1)
                        is StockMoveEvent.Cancelled ->
                            CancelledStockMove(state.moveId, state.order, state.revision + 1)
                        else -> state
                    }
                    is AssignedStockMove -> when (event) {
                        is StockMoveEvent.AssignShipped ->
                            ShippedStockMove(state.moveId, state.order,
                                event.shippedQty, state.revision + 1)
                        is StockMoveEvent.AssignShipFailed,
                        is StockMoveEvent.ShipFailed ->
                            ShipFailedStockMove(state.moveId, state.order,
                                state.revision + 1)
                        else -> state
                    }
                    is ShippedStockMove -> when (event) {
                        is StockMoveEvent.Arrived ->
                            ArrivedStockMove(state.moveId, state.order,
                                state.shippedQty, event.arrivedQty, state.revision + 1)
                        else -> state
                    }
                    is AssignFailedStockMove,
                    is ShipFailedStockMove,
                    is ArrivedStockMove,
                    is CancelledStockMove -> state
                }
    }
}

interface StockMoveEventIdentity {
    val moveId: StockMoveId
}

sealed class StockMoveEvent : Event, StockMoveEventIdentity {
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

interface Command<S>

sealed class StockMoveCommand : Command<StockMove> {
    data class Open(val order: StockMoveOrder) : StockMoveCommand()
    data class Assign(val moveId: StockMoveId) : StockMoveCommand()
    data class Cancel(val moveId: StockMoveId) : StockMoveCommand()
}

sealed class StockMoveReport : Command<StockMove> {
    data class Ship(val moveId: StockMoveId, val shippedQty: Quantity) : StockMoveReport()
    data class ShipFail(val moveId: StockMoveId) : StockMoveReport()
    data class Arrive(val moveId: StockMoveId, val arrivedQty: Quantity) : StockMoveReport()
}

typealias StoredEvent<E> = Pair<SeqNo, E>

interface EventStore<K, E> {
    fun events(key: K, startSeqNo: SeqNo = 0): List<StoredEvent<E>>
    fun storeEvent(key: K, event: E): SeqNo?
}

interface EventStoreSearch<E> {
    fun search(filter: (E) -> Boolean, startSeqNo: SeqNo = 0): List<StoredEvent<E>>
}

class MemoryEventStore<K, E>(val getId: (E) -> K) : EventStore<K, E>, EventStoreSearch<E> {
    private val store = mutableListOf<Pair<SeqNo, E>>()

    override fun search(filter: (E) -> Boolean, startSeqNo: SeqNo): List<StoredEvent<E>> =
        store.filter {
            it.first >= startSeqNo && filter(it.second)
        }

    override fun events(key: K, startSeqNo: SeqNo): List<StoredEvent<E>> =
        search({ getId(it) == key }, startSeqNo)

    override fun storeEvent(key: K, event: E): SeqNo? =
        store.add(Pair(store.size + 1L, event)).takeIf { it }?.let {
            store.last().first
        }
}

interface StockService {
    fun stock(item: ItemCode, location: LocationCode): Stock
}

interface StockMoveService {
    fun command(cmd: StockMoveCommand): StockMove?
    fun report(report: StockMoveReport): StockMove?
    fun status(moveId: StockMoveId): StockMove?
}

class SimpleStockService(private val eventStore: EventStoreSearch<StockMoveEvent>) : StockService {
    override fun stock(item: ItemCode, location: LocationCode): Stock =
        eventsForStock(item, location).fold(Stock(item, location)) { acc, event ->
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
                is StockMoveEvent.AssignShipFailed -> acc.copy(
                    assignedQty = acc.assignedQty - event.assignedQty,
                    availableQty = acc.availableQty + event.assignedQty
                )
                is StockMoveEvent.Arrived -> acc.copy(
                    qty = acc.qty + event.arrivedQty,
                    availableQty = acc.availableQty + event.arrivedQty
                )
                else -> acc
            }
        }

    private fun eventsForStock(item: ItemCode, location: LocationCode): List<StockMoveEvent> =
        eventStore.search({
          when (it) {
              is StockMoveEvent.Assigned -> it.item == item && it.location == location
              is StockMoveEvent.AssignShipped -> it.item == item && it.location == location
              is StockMoveEvent.AssignShipFailed -> it.item == item && it.location == location
              is StockMoveEvent.Shipped -> it.item == item && it.location == location
              is StockMoveEvent.Arrived -> it.item == item && it.location == location
              else -> false
          }
        }).map {
            it.second
        }
}

class SimpleStockMoveService(
    private val eventStore: EventStore<StockMoveId, StockMoveEvent>,
    private val stockService: StockService
) : StockMoveService {
    override fun command(cmd: StockMoveCommand): StockMove? =
        when (cmd) {
            is StockMoveCommand.Open -> updateState(emptyMoveId) { toEvent(cmd, it) }
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
        restoreState(moveId).takeIf { it != StockMove.EmptyStockMove }

    private fun restoreState(moveId: StockMoveId): StockMove =
        eventStore.events(moveId).map {
            it.second
        }.fold(StockMove.EmptyStockMove as StockMove) { acc, event ->
            StockMove.applyEvent(acc, event)
        }

    private fun createMoveId(): StockMoveId = "stock-move:${UUID.randomUUID()}"

    private fun toEvent(cmd: StockMoveCommand.Open, state: StockMove): StockMoveEvent? =
        if (cmd.order.item.isNotBlank() &&
            cmd.order.from.isNotBlank() &&
            cmd.order.to.isNotBlank() &&
            cmd.order.qty > 0)
            StockMoveEvent.Opened(createMoveId(), cmd.order)
        else null

    private fun toEvent(cmd: StockMoveCommand.Assign, state: StockMove): StockMoveEvent? {
        val order = state.order
        val stock = stockService.stock(order.item, order.from)

        val event =
            if (order.qty <= stock.availableQty)
                StockMoveEvent.Assigned(state.moveId, order.item, order.from, order.qty)
            else
                StockMoveEvent.AssignFailed(state.moveId, order.item, order.from, order.qty)

        return event
    }

    private fun toEvent(rep: StockMoveReport.Ship, state: StockMove): StockMoveEvent? =
        if (state is Assigning)
            StockMoveEvent.AssignShipped(state.moveId, state.order.item,
                state.order.from, state.assignedQty, rep.shippedQty)
        else
            StockMoveEvent.Shipped(state.moveId, state.order.item,
                state.order.from, rep.shippedQty)

    private fun toEvent(rep: StockMoveReport.ShipFail, state: StockMove): StockMoveEvent? =
        if (state is Assigning)
            StockMoveEvent.AssignShipFailed(state.moveId, state.order.item,
                state.order.from, state.assignedQty)
        else
            StockMoveEvent.ShipFailed(state.moveId, state.order.item,
                state.order.from)

    private fun toEvent(rep: StockMoveReport.Arrive, state: StockMove): StockMoveEvent? =
        StockMoveEvent.Arrived(rep.moveId, state.order.item, state.order.to, rep.arrivedQty)

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

    companion object {
        const val emptyMoveId: StockMoveId = ""
    }
}

fun main() {
    val eventStore = MemoryEventStore(StockMoveEvent::moveId)

    val stockService = SimpleStockService(eventStore)
    val moveService = SimpleStockMoveService(eventStore, stockService)

    val item1 = "item-1"
    val loc0 = "adjustment-0"
    val loc1 = "location-1"
    val loc2 = "location-2"

    moveService.command(StockMoveCommand.Open(StockMoveOrder(item1, 30, loc0, loc1)))?.let {
        println(it)

        println( moveService.report(StockMoveReport.Arrive(it.moveId, it.order.qty)) )

        println( stockService.stock(item1, loc0) )
        println( stockService.stock(item1, loc1) )
    }

    println("----------------")

    moveService.command(StockMoveCommand.Open(StockMoveOrder(item1, 2, loc1, loc2)))?.let {
        println(it)

        println( moveService.command(StockMoveCommand.Assign(it.moveId)) )

        println( stockService.stock(item1, loc1) )

        println( moveService.report(StockMoveReport.Ship(it.moveId, it.order.qty)) )

        println( stockService.stock(item1, loc1) )
        println( stockService.stock(item1, loc2) )

        println( moveService.report(StockMoveReport.Arrive(it.moveId, it.order.qty)) )

        println(stockService.stock(item1, loc1))
        println(stockService.stock(item1, loc2))
    }
}