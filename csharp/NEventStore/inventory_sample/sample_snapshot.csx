
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
	public string NewName { get; }

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

class InventoryItem
{
	public string Id { get; }
	public string Name { get; }
	public int Count { get; }

	public InventoryItem() : this(null, null, 0) {
	}

	private InventoryItem(string id, string name, int count)
	{
		this.Id = id;
		this.Name = name;
		this.Count = count;
	}

	public InventoryItem Apply(InventoryItemCreated ev)
	{
		return new InventoryItem(ev.Id, null, 0);
	}

	public InventoryItem Apply(InventoryItemRenamed ev)
	{
		return new InventoryItem(Id, ev.NewName, Count);
	}

	public InventoryItem Apply(ItemsCheckedInToInventory ev)
	{
		return new InventoryItem(Id, Name, Count + ev.Count);
	}

	public override string ToString()
	{
		return "InventoryItem(id: " + Id + ", name: " + Name + ", count: " + Count + ")";
	}
}

Func<Object, EventMessage> CreateEventMessage = ev => new EventMessage { Body = ev };

Func<InventoryItem, ICollection<EventMessage>, InventoryItem> ApplyEvents = (obj, events) => events.Aggregate(obj, (acc, ev) => acc.Apply((dynamic)ev.Body));

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
		var entity = ApplyEvents(new InventoryItem(), eventStream.CommittedEvents);
		var snapshot = new Snapshot(aggregateId, eventStream.StreamRevision, entity);

		eventStore.Advanced.AddSnapshot(snapshot);
	}

	using (var eventStream = eventStore.OpenStream(aggregateId))
	{
		eventStream.Add( CreateEventMessage(new ItemsCheckedInToInventory(3)) );
		eventStream.CommitChanges(Guid.NewGuid());
	}

	using (var eventStream = eventStore.OpenStream(aggregateId))
	{
		Console.WriteLine("----- EventStream CommittedEvents -----");

		foreach (var ev in eventStream.CommittedEvents)
		{
			Console.WriteLine(ev.Body);
		}
	}

	Console.WriteLine("----- Snapshot -----");

	var latestSnapshot = eventStore.Advanced.GetSnapshot(aggregateId, int.MaxValue);

	Console.WriteLine("payload: " + latestSnapshot.Payload);

	using (var eventStream = eventStore.OpenStream(latestSnapshot, int.MaxValue))
	{
		Console.WriteLine("----- Snapshot EventStream CommittedEvents -----");

		foreach (var ev in eventStream.CommittedEvents)
		{
			Console.WriteLine(ev.Body);
		}

		Console.WriteLine("----- Snapshot Applied -----");

		Console.WriteLine(ApplyEvents((dynamic)latestSnapshot.Payload, eventStream.CommittedEvents));
	}
}

