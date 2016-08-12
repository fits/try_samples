using System;
using NEventStore;

var aggregateId = "s1";
var name = "sample1";

class InventoryItemCreated
{
	public string Id { get; }

	public InventoryItemCreated(string id)
	{
		this.Id = id;
	}
}

class InventoryItemRenamed
{
	public String NewName { get; }

	public InventoryItemRenamed(string newName)
	{
		this.NewName = newName;
	}
}

class ItemsCheckedInToInventory
{
	public int Count { get; }

	public ItemsCheckedInToInventory(int count)
	{
		this.Count = count;
	}
}

Func<Object, EventMessage> CreateEventMessage = ev => new EventMessage { Body = ev };

using (var eventStore = Wireup.Init().Build())
{
	using (var eventStream = eventStore.OpenStream(aggregateId))
	{
		eventStream.Add( CreateEventMessage(new InventoryItemCreated(aggregateId)) );

		eventStream.Add( CreateEventMessage(new InventoryItemRenamed(name)) );

		eventStream.CommitChanges(Guid.NewGuid());
	}

	using (var eventStream = eventStore.OpenStream(aggregateId))
	{
		eventStream.Add( CreateEventMessage(new ItemsCheckedInToInventory(5)) );
		eventStream.CommitChanges(Guid.NewGuid());
	}

	using (var eventStream = eventStore.OpenStream(aggregateId))
	{
		eventStream.Add( CreateEventMessage(new ItemsCheckedInToInventory(3)) );
		eventStream.CommitChanges(Guid.NewGuid());
	}


	using (var eventStream = eventStore.OpenStream(aggregateId))
	{
		Console.WriteLine("----- CommittedEvents -----");

		foreach (var ev in eventStream.CommittedEvents)
		{
			Console.WriteLine(ev.Body);
		}
	}
}

