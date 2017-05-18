
const AWS = require('aws-sdk');

AWS.config.loadFromPath('./config.json');

const dynamo = new AWS.DynamoDB({endpoint: 'http://localhost:4567'})

const table = process.argv[2];

const id = process.argv[3];
const name = process.argv[4];

const data = {
	TableName: table,
	Item: {
		Id: { S: id },
		Name: { S: name }
	}
};

dynamo.putItem(data).promise()
	.then(r => {
		const query = {
			TableName: table,
			Key: {
				Id: { S: id }
			}
		};

		return dynamo.getItem(query).promise();
	})
	.then(console.log)
	.catch(console.error);
