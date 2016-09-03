package sample

import akka.persistence._

case class CounterAdd(count: Int)
case class AddedCounter(count: Int)

class SampleActor extends PersistentActor {
  var state = 0

  def updateState(event: AddedCounter): Unit = {
    state += event.count
  }

  override def persistenceId: String = "counter"

  override def receiveRecover: Receive = {
    case ev: AddedCounter => {
      println(s"*** receiveRecover - event: ${ev}")

      updateState(ev)
    }
    case SnapshotOffer(_, snapshot: Int) => {
      println(s"*** receiveRecover - snapshot: ${snapshot}")

      state = snapshot
    }
  }

  override def receiveCommand: Receive = {
    case cmd: CounterAdd => persist(AddedCounter(cmd.count)) { event =>
      println("*** persist event")

      updateState(event)
      context.system.eventStream.publish(event)
    }
    case "snapshot" => saveSnapshot(state)
    case "dump" => println(s"counter: ${state}")
    case "end" => context.stop(self)
  }
}
