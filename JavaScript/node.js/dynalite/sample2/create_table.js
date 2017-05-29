
const AWS = require('aws-sdk');

AWS.config.loadFromPath('./config.json');

const dynamo = new AWS.DynamoDB({endpoint: 'http://localhost:4567'})

const table = 'events';

dynamo.listTables().promise()
	.then(tbls => {
		if (!tbls.TableNames.includes(table)) {
			const param = {
				TableName: table,
				KeySchema: [
					{ AttributeName: 'aggregateId', KeyType: 'HASH' },
					{ AttributeName: 'rowKey', KeyType: 'RANGE' }
				],
				AttributeDefinitions: [
					{ AttributeName: 'aggregateId', AttributeType: 'S' },
					{ AttributeName: 'rowKey', AttributeType: 'S' }
				],
				ProvisionedThroughput: {
					ReadCapacityUnits: 1,
					WriteCapacityUnits: 1
				}
			};
			return dynamo.createTable(param).promise();
		}

		return null;
	})
	.then(d => dynamo.describeTable({TableName: table}).promise())
	.then(console.log)
	.catch(console.error);
