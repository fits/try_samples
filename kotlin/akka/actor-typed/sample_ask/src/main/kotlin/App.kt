
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.*
import akka.japi.function.Function
import java.time.Duration

data class Counter(val value: Int)

sealed class CounterCommand {
    data class Init(val value: Int) : CounterCommand()
    data class Up(val value: Int) : CounterCommand()
    data class Get(val replyTo: ActorRef<Counter>) : CounterCommand()
}

class CounterBehavior(context: ActorContext<CounterCommand>) :
        AbstractBehavior<CounterCommand>(context) {
    private var value: Int = 0

    override fun createReceive(): Receive<CounterCommand> =
            newReceiveBuilder()
                    .onAnyMessage {
                        when (it) {
                            is CounterCommand.Init ->
                                value = it.value
                            is CounterCommand.Up ->
                                value += it.value
                            is CounterCommand.Get ->
                                it.replyTo.tell(Counter(value))
                        }
                        Behaviors.same()
                    }
                    .build()

    companion object {
        fun create(): Behavior<CounterCommand> =
                Behaviors.setup(::CounterBehavior)
    }
}

fun main() {
    val system = ActorSystem.create(CounterBehavior.create(), "counter")

    system.tell(CounterCommand.Init(1))
    system.tell(CounterCommand.Up(2))

    val res = AskPattern.ask<CounterCommand, Counter>(
            system,
            Function { CounterCommand.Get(it) },
            Duration.ofSeconds(5),
            system.scheduler()
    )

    res.whenComplete { d, e ->
        println(d)
        e?.printStackTrace()

        system.terminate()
    }
}