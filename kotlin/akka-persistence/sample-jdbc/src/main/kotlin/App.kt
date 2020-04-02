
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.AskPattern
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.javadsl.CommandHandler
import akka.persistence.typed.javadsl.EventHandler
import akka.persistence.typed.javadsl.EventSourcedBehavior
import java.time.Duration
import akka.japi.function.Function as JFunction
import com.fasterxml.jackson.annotation.JsonTypeInfo

typealias DataId = String

interface JsonSerializable

sealed class DataCommand : JsonSerializable {
    data class Init(val initValue: Int) : DataCommand()
    data class Add(val value: Int) : DataCommand()
    data class Get(val replyTo: ActorRef<Data>) : DataCommand()
}

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
sealed class DataEvent : JsonSerializable {
    data class Initialized(val value: Int) : DataEvent()
    data class Added(val addedValue: Int) : DataEvent()
}

data class Data(val id: DataId, val value: Int = 0) : JsonSerializable

class DataBehavior(private val id: DataId) :
        EventSourcedBehavior<DataCommand, DataEvent, Data>(PersistenceId.ofUniqueId(id)) {

    override fun emptyState(): Data = Data(id)

    override fun commandHandler(): CommandHandler<DataCommand, DataEvent, Data> =
            newCommandHandlerBuilder()
                    .forAnyState()
                    .onAnyCommand(::handleCommand)

    override fun eventHandler(): EventHandler<Data, DataEvent> =
            newEventHandlerBuilder()
                    .forAnyState()
                    .onAnyEvent(::handleEvent)

    private fun handleCommand(state: Data, cmd: DataCommand) =
            when (cmd) {
                is DataCommand.Init ->
                    if (cmd.initValue >= 0)
                        Effect().persist(DataEvent.Initialized(cmd.initValue))
                    else
                        Effect().unhandled()
                is DataCommand.Add ->
                    if (cmd.value > 0)
                        Effect().persist(DataEvent.Added(cmd.value))
                    else
                        Effect().unhandled()
                is DataCommand.Get ->
                    Effect().reply(cmd.replyTo, state)
            }

    private fun handleEvent(state: Data, event: DataEvent) =
            when (event) {
                is DataEvent.Initialized -> state.copy(value = event.value)
                is DataEvent.Added -> state.copy(value = state.value + event.addedValue)
            }

    companion object {
        fun create(id: DataId) = DataBehavior(id)
    }
}

fun main() {
    val system = ActorSystem.create(DataBehavior.create("d1"), "data")

    system.tell(DataCommand.Init(1))
    system.tell(DataCommand.Add(5))

    val res = AskPattern.ask<DataCommand, Data>(
            system,
            JFunction { DataCommand.Get(it) },
            Duration.ofSeconds(5),
            system.scheduler()
    )

    res.whenComplete { state, err ->
        println(state)
        err?.printStackTrace()

        system.terminate()
    }
}
