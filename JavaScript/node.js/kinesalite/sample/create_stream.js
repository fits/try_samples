
const AWS = require('aws-sdk');

AWS.config.loadFromPath('./config.json');

const kinesis = new AWS.Kinesis({endpoint: 'http://localhost:4567'})

const stream = process.argv[2];

kinesis.listStreams().promise()
	.then( sts => {
		if (!sts.StreamNames.includes(stream)) {
			return kinesis.createStream({
				ShardCount: 1,
				StreamName: stream
			}).promise();
		}
	})
	.then( r => kinesis.describeStream({StreamName: stream}).promise())
	.then(console.log)
	.catch(console.error);
