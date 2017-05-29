'use strict';

const AWS = require('aws-sdk');
const uuidV1 = require('uuid/v1');

const dynamo = new AWS.DynamoDB();

const table = 'SampleTable';

module.exports.updateValue = (event, context, callback) => {
	if (event.body) {
		const data = JSON.parse(event.body);

		const param = {
			TableName: table,
			Item: {
				aggregateId: { S: data.id },
				rowKey: { S: uuidV1() },
				value: { N: `${data.value}` }
			}
		};

		dynamo.putItem(param).promise()
			.then(r => callback(null, { body: JSON.stringify(r) }))
			.catch(callback);

		return;
	}

	callback(null, { statusCode: 400 });
};

module.exports.getValue = (event, context, callback) => {

	if (event.pathParameters && event.pathParameters.id) {
		const id = event.pathParameters.id;

		const param = {
			TableName: table,
			KeyConditionExpression: 'aggregateId = :id',
			ExpressionAttributeValues: { ':id': { S: id } },
			ScanIndexForward: false,
			Limit: 1
		};

		dynamo.query(param).promise()
			.then(rs => rs.Items.reduce(
				(acc, d) => {
					acc.value = parseInt(d.value.N);
					return acc;
				},
				{ id: id, value: 0 }
			))
			.then(r => callback(null, { body: JSON.stringify(r) }))
			.catch(callback);

		return;
	}

	callback(null, { statusCode: 400 });
};
