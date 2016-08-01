'use strict';

//const es = require('eventstore')();
const es = require('eventstore')({ type: 'redis' });

const aggregate = (events, snapshot) => {
	if (snapshot == null) {
		snapshot = { data: { value: 0 } };
	}

	return events.reduce(
		(acc, ev) => new Object({ value: acc.value + ev.payload.value }),
		snapshot.data
	);
};

es.on('connect', () => {
	console.log('*** connect');

	es.getEventStream('s1', (err, stream) => {
		stream.addEvent({ value: 1 });
		stream.addEvent({ value: 2 });
		stream.addEvent({ value: 3 });

		stream.commit();

		es.getFromSnapshot('s1', (err, snapshot, stream) => {
			console.log(`snapshot: ${snapshot}, event length: ${stream.events.length}`);

			const d = aggregate(stream.events, snapshot);

			console.log(d);

			es.createSnapshot(
				{
					streamId: 's1',
					data: d,
					revision: stream.lastRevision
				},
				(err) => {
					es.getFromSnapshot('s1', (err, ss, st) => 
						console.log(`snapshot: ${ss}, event length: ${st.events.length}`) );
				}
			);

		});
	});
});

es.on('disconnect', () => console.log('*** disconnect') );

es.init();
