
import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.instances.monad
import arrow.free.*
import arrow.free.instances.*
import arrow.typeclasses.binding

data class Stock(val id: String, val quantity: Int = 0)

class ForEvent private constructor() { companion object }
typealias EventOf<A> = arrow.Kind<ForEvent, A>

sealed class Event<out A>: EventOf<A> {
    data class Created(val id: String): Event<String>()
    data class Added(val id: String, val quantity: Int): Event<Unit>()
    data class Removed(val id: String, val quantity: Int): Event<Unit>()
}

typealias Command<A> = Free<ForEvent, A>

fun create(id: String): Command<String> = Free.liftF(Event.Created(id))
fun add(id: String, quantity: Int): Command<Unit> = Free.liftF(Event.Added(id, quantity))
fun remove(id: String, quantity: Int): Command<Unit> = Free.liftF(Event.Removed(id, quantity))

operator fun Stock.plus(diff: Int) = copy(quantity = quantity + diff)
operator fun Stock.minus(diff: Int) = copy(quantity = quantity - diff)

fun Map<String, Stock>.update(ev: Event.Added) =
        this + (ev.id to (getOrDefault(ev.id, Stock(ev.id)) + ev.quantity))

fun Map<String, Stock>.update(ev: Event.Removed) =
        this + (ev.id to (getOrDefault(ev.id, Stock(ev.id)) - ev.quantity))

object Step: FunctionK<ForEvent, StatePartialOf<Map<String, Stock>>> {
    @Suppress("UNCHECKED_CAST")
    override fun <A> invoke(fa: Kind<ForEvent, A>): Kind<StatePartialOf<Map<String, Stock>>, A> {
        println(fa)
        return when (fa) {
            is Event.Created -> State { s -> (s + (fa.id to Stock(fa.id))) toT fa.id as A }
            is Event.Added -> State { s -> s.update(fa) toT Unit as A }
            is Event.Removed -> State { s -> s.update(fa) toT Unit as A }
            else -> State { s -> s toT Unit as A }
        }
    }
}

fun <A> interpret(cmd: Command<A>): State<Map<String, Stock>, A> =
    cmd.foldMap(Step, StateApi.monad()).fix()

fun main(args: Array<String>) {
    val proc = ForFree<ForEvent>() extensions {
        binding {
            val id1 = create("id-1").bind()
            val id2 = create("id-2").bind()
            add(id1, 5).bind()
            remove(id1, 2).bind()
            add(id2, 2).bind()
            add(id1, 10).bind()
            id1 toT id2
        }
    }

    println(proc)

    val r = interpret(proc.fix()).run(HashMap())

    println(r)
}