
import akka.actor.typed.*
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.javadsl.ClusterSharding
import akka.cluster.sharding.typed.javadsl.Entity
import akka.cluster.sharding.typed.javadsl.EntityTypeKey
import java.time.Duration

sealed class CounterCommand {
    data class Init(val value: Int) : CounterCommand()
    data class Up(val incValue: Int) : CounterCommand()
    data class Get(val replyTo: ActorRef<Counter>) : CounterCommand()
}

data class Counter(val id: String, val value: Int)

class CounterActor(context: ActorContext<CounterCommand>, val id: String) : AbstractBehavior<CounterCommand>(context) {
    private var value: Int = 0

    override fun createReceive(): Receive<CounterCommand> =
            newReceiveBuilder()
                    .onAnyMessage {
                        when (it) {
                            is CounterCommand.Init -> value = it.value
                            is CounterCommand.Up -> value += it.incValue
                            is CounterCommand.Get -> it.replyTo.tell(Counter(id, value))
                        }

                        Behaviors.same()
                    }
                    .onSignal(PreRestart::class.java) {
                        println("*** signal: $it, ${this.context.self.path()}")
                        Behaviors.same()
                    }
                    .onSignal(PostStop::class.java) {
                        println("*** signal: $it, ${this.context.self.path()}")
                        Behaviors.same()
                    }
                    .onSignal(Terminated::class.java) {
                        println("*** signal: $it, ${this.context.self.path()}")
                        Behaviors.same()
                    }
                    .build()

    companion object {
        fun create(id: String): Behavior<CounterCommand> = Behaviors.setup {
            CounterActor(it, id)
        }
    }
}

fun main() {
    val system = ActorSystem.create(Behaviors.empty<CounterActor>(), "counter")
    val sharding = ClusterSharding.get(system)

    val typeKey = EntityTypeKey.create(CounterCommand::class.java, "Counter")

    val shardRegion = sharding.init(
            Entity.of(typeKey) { CounterActor.create(it.entityId) }
    )

    val c1 = sharding.entityRefFor(typeKey, "c1")

    c1.tell(CounterCommand.Init(0))
    c1.tell(CounterCommand.Up(1))
    c1.tell(CounterCommand.Up(2))

    val r1 = c1.ask<Counter>({ CounterCommand.Get(it) }, Duration.ofSeconds(1))

    println( r1.toCompletableFuture().get() )

    shardRegion.tell(ShardingEnvelope("c2", CounterCommand.Init(5)))
    shardRegion.tell(ShardingEnvelope("c2", CounterCommand.Up(2)))

    val r2 = sharding.entityRefFor(typeKey, "c2")
                        .ask<Counter>({ CounterCommand.Get(it) }, Duration.ofSeconds(1))

    println( r2.toCompletableFuture().get() )

    system.terminate()
}