
const AWS = require('aws-sdk');

AWS.config.loadFromPath('./config.json');

const dynamo = new AWS.DynamoDB({endpoint: 'http://localhost:4567'})

const table = process.argv[2];

dynamo.listTables().promise()
	.then(tbls => {
		if (!tbls.TableNames.includes(table)) {
			const param = {
				TableName: table,
				KeySchema: [
					{ AttributeName: 'Id', KeyType: 'HASH' }
				],
				AttributeDefinitions: [
					{ AttributeName: 'Id', AttributeType: 'S' }
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
