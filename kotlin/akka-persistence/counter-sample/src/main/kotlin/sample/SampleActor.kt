package sample

import akka.japi.pf.ReceiveBuilder
import akka.persistence.AbstractPersistentActor
import akka.persistence.SnapshotOffer

data class CounterAdd(val count: Int)
data class AddedCounter(val count: Int)

class SampleActor(val id: String) : AbstractPersistentActor() {
	private var state = 0

	override fun persistenceId(): String = id

	override fun receiveRecover() = 
		ReceiveBuilder
			.match(AddedCounter::class.java) { event ->
				updateState(event)
			}
			.match(SnapshotOffer::class.java) { ss ->
				state = ss.snapshot() as Int
			}
			.build()

	override fun receiveCommand() = 
		ReceiveBuilder
			.match(CounterAdd::class.java) { cmd ->
				val f: (AddedCounter) -> Unit = { event ->
					updateState(event)
					context.system().eventStream().publish(event)
				}

				persist(AddedCounter(cmd.count), f)
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
