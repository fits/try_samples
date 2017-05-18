
const AWS = require('aws-sdk');

AWS.config.loadFromPath('./config.json');

const kinesis = new AWS.Kinesis({endpoint: 'http://localhost:4567'})

const stream = process.argv[2];

kinesis.describeStream({StreamName: stream}).promise()
	.then(s => s.StreamDescription.Shards[0].ShardId)
	.then(sid => {
		const param = {
			StreamName: stream,
			ShardId: sid,
			ShardIteratorType: 'TRIM_HORIZON'
		};

		return kinesis.getShardIterator(param).promise();
	})
	.then(sit => kinesis.getRecords(sit).promise())
	.then(r => r.Records.map(rec => {
		return {
			seqno: rec.SequenceNumber,
			timestamp: rec.ApproximateArrivalTimestamp,
			key: rec.PartitionKey,
			data: rec.Data.toString()
		};
	}))
	.then(console.log)
	.catch(console.error);
