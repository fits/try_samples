
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive

class PrintActor(context: ActorContext<String>) : AbstractBehavior<String>(context) {
    override fun createReceive(): Receive<String> =
            newReceiveBuilder()
                    .onMessageEquals("sample", ::onSample)
                    .onAnyMessage {
                        println("PrintActor.onAnyMessage: $it")
                        Behaviors.same()
                    }
                    .build()

    private fun onSample(): Behavior<String> {
        println("PrintActor.onSample")
        return Behaviors.same()
    }

    companion object {
        fun create(): Behavior<String> = Behaviors.setup(::PrintActor)
    }
}

sealed class SampleCommand {
    data class Send(val message: String) : SampleCommand()
    data class SendValue(val value: Int) : SampleCommand()
}

class SampleActor(context: ActorContext<SampleCommand>) :
        AbstractBehavior<SampleCommand>(context) {

    private val actor = context.spawn(PrintActor.create(), "print-actor")

    override fun createReceive(): Receive<SampleCommand> =
            newReceiveBuilder()
                    .onAnyMessage {
                        println("SampleActor.onAnyMessage: $it")

                        when (it) {
                            is SampleCommand.Send -> actor.tell(it.message)
                            is SampleCommand.SendValue ->
                                actor.tell("value-${it.value}")
                        }

                        Behaviors.same()
                    }
                    .build()

    companion object {
        fun create(): Behavior<SampleCommand> = Behaviors.setup(::SampleActor)
    }
}

fun main() {
    val system = ActorSystem.create(SampleActor.create(), "sampleSystem")

    system.tell(SampleCommand.Send("message1"))
    system.tell(SampleCommand.Send("sample"))
    system.tell(SampleCommand.SendValue(123))

    system.terminate()
}