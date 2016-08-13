'use strict';

const es = require('eventstore')(); // in-memory
//const es = require('eventstore')({ type: 'redis' });

class InventoryItemCreated {
	constructor(id) {
		this.id = id;
	}
}

class InventoryItemRenamed {
	constructor(newName) {
		this.newName = newName;
	}
}

class ItemsCheckedInToInventory {
	constructor(count) {
		this.count = count;
	}
}

class InventoryItem {
	constructor(id, name, count) {
		this.id = id;
		this.name = name;

		if (!count) {
			count = 0;
		}

		this.count = count;
	}
}

const apply = (item, event) => {
	const id = (event.hasOwnProperty('id')) ? event.id : item.id;
	const name = (event.hasOwnProperty('newName')) ? event.newName : item.name;
	const count = (event.hasOwnProperty('count')) ? event.count : 0;

	return new InventoryItem(id, name, item.count + count);
};

const aggregate = (events, snapshot) => {
	if (snapshot == null) {
		snapshot = { data: new InventoryItem() };
	}

	return events.reduce(
		(acc, ev) => apply(acc, ev.payload),
		snapshot.data
	);
};

const eventStream = streamId => new Promise((s, e) => 
	es.getEventStream(streamId, (err, stream) => {
		if (err) {
			e(err);
		}
		else {
			s(stream);
		}
	})
);

const fromSnapshot = streamId => new Promise((s, e) =>
	es.getFromSnapshot(streamId, (err, snapshot, stream) => {
		if (err) {
			e(err);
		}
		else {
			s({ snapshot: snapshot, stream: stream });
		}
	})
);

const createSnapshot = (streamId, payload, revision) => new Promise((s, e) =>
	es.createSnapshot({
		streamId: streamId,
		data: payload,
		revision: revision
	}, (err) => {
		if (err) {
			e(err);
		}
		else {
			s();
		}
	})
);

const streamId = 's1';

es.on('connect', () => {
	console.log('*** connect');

	eventStream(streamId).then(stream => {
		stream.addEvent( new InventoryItemCreated(streamId) );
		stream.addEvent( new InventoryItemRenamed('sample1') );

		stream.commit();

		stream.addEvent( new ItemsCheckedInToInventory(5) );

		console.log('----- event -----');
		stream.events.forEach(ev => console.log(ev.payload));

		console.log('----- uncommitted event -----');
		stream.uncommittedEvents.forEach(ev => console.log(ev.payload));

		stream.commit();

		return fromSnapshot(streamId);

	}).then(d => {
		const snapshot = d.snapshot;
		const stream = d.stream;

		console.log('----- snapshot -----');
		console.log(snapshot);

		console.log('----- event -----');
		stream.events.forEach(ev => console.log(ev.payload));

		console.log('----- uncommitted event -----');
		stream.uncommittedEvents.forEach(ev => console.log(ev.payload));

		const data = aggregate(stream.events, snapshot);

		createSnapshot(streamId, data, stream.lastRevision)
			.then(() => console.log('*** created snapshot'));

		stream.addEvent( new ItemsCheckedInToInventory(3) );

		stream.commit();

		return fromSnapshot(streamId);

	}).then(d => {
		const snapshot = d.snapshot;
		const stream = d.stream;

		console.log('----- snapshot -----');
		console.log(snapshot);

		console.log('----- event -----');
		stream.events.forEach(ev => console.log(ev.payload));

		console.log('----------');

		console.log(aggregate(stream.events, snapshot));

	}).catch(e => 
		console.error(e)
	);

});

es.on('disconnect', () => console.log('*** disconnect') );

es.init();
