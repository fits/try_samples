'use strict';

const AWS = require('aws-sdk');

const dynamo = new AWS.DynamoDB();

const table = 'SampleTable';

module.exports.updateValue = (event, context, callback) => {

	if (event.body) {
		const data = JSON.parse(event.body);

		const param = {
			TableName: table,
			Item: {
				aggregateId: { S: data.id },
				rowKey: { S: new Date().toISOString() },
				value: { N: `${data.value}` }
			}
		};

		dynamo.putItem(param).promise()
			.then(r => done(callback, r))
			.catch(callback);

		return;
	}

	clientError(callback);
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
			.then(r => done(callback, r))
			.catch(callback);

		return;
	}

	clientError(callback);
};

const done = (callback, res) => callback(null, {
	headers: {'Access-Control-Allow-Origin': '*'},
	body: JSON.stringify(res)
});

const clientError = callback => callback(null, { statusCode: 400 });
