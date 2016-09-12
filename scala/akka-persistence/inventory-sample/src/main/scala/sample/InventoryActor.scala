package sample

import akka.persistence._
import sample.commands.{CheckInItemsToInventory, CreateInventoryItem}
import sample.events.{InventoryItemCreated, InventoryItemRenamed, ItemsCheckedInToInventory}
import sample.models.InventoryItem

class InventoryActor(val id: String) extends PersistentActor {

  var state = Option.empty[InventoryItem]

  override def persistenceId: String = id

  override def receiveRecover: Receive = {
    case event: InventoryItemCreated => updateState(event)
    case event: InventoryItemRenamed => updateState(event)
    case event: ItemsCheckedInToInventory => updateState(event)
    case SnapshotOffer(_, snapshot: InventoryItem) => {
      println(s"*** receiveRecover - snapshot: ${snapshot}")
      state = Some(snapshot)
    }
  }

  override def receiveCommand: Receive = {
    case cmd: CreateInventoryItem => if (id eq cmd.id) {
      persist(
        InventoryItemCreated(cmd.id),
        InventoryItemRenamed(cmd.name)
      ) { events =>

        updateState(events._1)
        updateState(events._2)

        context.system.eventStream.publish(events)
      }
    }
    case cmd: CheckInItemsToInventory => if (id eq cmd.id) {
      persist(ItemsCheckedInToInventory(cmd.count)) { event =>
        updateState(event)

        context.system.eventStream.publish(event)
      }
    }
    case "snapshot" => state.foreach(saveSnapshot)
    case "dump" => println(s"state: ${state}")
    case "end" => context.system.terminate()
  }

  private def updateState(event: InventoryItemCreated): Unit = {
    println(s"*** updateState: ${event}")

    state = Some(InventoryItem(event.id))
  }

  private def updateState(event: InventoryItemRenamed): Unit = {
    println(s"*** updateState: ${event}")

    state = state.map(st => InventoryItem(st.id, event.newName))
  }

  private def updateState(event: ItemsCheckedInToInventory): Unit = {
    println(s"*** updateState: ${event}")

    state = state.map(st => InventoryItem(st.id, st.name, st.count + event.count))
  }
}
