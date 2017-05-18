
const AWS = require('aws-sdk');
AWS.config.loadFromPath('./config.json');

const es = require('eventstore')({
	type: 'dynamodb'
});

const streamId = process.argv[2];
const count = parseInt(process.argv[3]);

es.on('connect', () => console.log('connected'));

es.on('disconnect', () => console.log('disconnected'));

es.init(console.error);

es.getEventStream(streamId, (err, stream) => {
	stream.addEvent({countup: count});
	stream.commit();

	es.getEvents(streamId, (err, events) => events.forEach(console.log));
});
