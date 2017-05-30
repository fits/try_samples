
const AWS = require('aws-sdk');

AWS.config.loadFromPath('./config.json');

const dynamo = new AWS.DynamoDB({endpoint: 'http://localhost:4567'})

const table = 'events';

const id = process.argv[2];
const value = process.argv[3];

const data = {
	TableName: table,
	Item: {
		aggregateId: { S: id },
		rowKey: { S: new Date().toISOString() },
		value: { N: value }
	}
};

dynamo.putItem(data).promise()
	.then(console.log)
	.catch(console.error);
