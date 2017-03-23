package sample

import akka.persistence.AbstractPersistentActor
import akka.persistence.SnapshotOffer

data class CounterAdd(val count: Int)
data class AddedCounter(val count: Int)

class SampleActor(val id: String) : AbstractPersistentActor() {
    private var state = 0

    override fun persistenceId(): String = id

    override fun createReceiveRecover() = receiveBuilder()
            .match(AddedCounter::class.java) { event ->
                updateState(event)
            }
            .match(SnapshotOffer::class.java) { ss ->
                state = ss.snapshot() as Int
            }
            .build()

    override fun createReceive() = receiveBuilder()
            .match(CounterAdd::class.java) { cmd ->
                persist(AddedCounter(cmd.count)) { event ->
                    updateState(event)
                    context.system().eventStream().publish(event)
                }

            }
            .matchEquals("snapshot") {
                saveSnapshot(state)
            }
            .matchEquals("dump") {
                println("counter: ${state}")
            }
            .matchEquals("end") {
                context.stop(self())
            }
            .build()

    private fun updateState(event: AddedCounter) {
        state += event.count
    }
}
