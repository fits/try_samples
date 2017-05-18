
const AWS = require('aws-sdk');

AWS.config.loadFromPath('./config.json');

const kinesis = new AWS.Kinesis({endpoint: 'http://localhost:4567'})

const stream = process.argv[2];
const key = process.argv[3];
const value = process.argv[4];

const data = {
	StreamName: stream,
	PartitionKey: key,
	Data: value
};

kinesis.putRecord(data).promise()
	.then(console.log)
	.catch(console.error);
