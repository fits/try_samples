
const AWS = require('aws-sdk');
const uuidV1 = require('uuid/v1');

AWS.config.loadFromPath('./config.json');

const dynamo = new AWS.DynamoDB({endpoint: 'http://localhost:4567'})

const table = 'events';

const id = process.argv[2];

const param = {
	TableName: table,
	KeyConditionExpression: 'aggregateId = :id',
	ExpressionAttributeValues: { ':id': { S: id } }
};

const dump = d => {
	console.log(d);
	return d;
};

dynamo.query(param).promise()
	.then(dump)
	.then(ds => console.log(ds.Items))
	.catch(console.error);
