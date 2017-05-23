
const fs = require('fs');
const AWS = require('aws-sdk');

AWS.config.loadFromPath('./config.json');

const lambda = new AWS.Lambda();

const runtime = 'nodejs6.10';

const name = process.argv[2];
const handler = process.argv[3];
const role = process.argv[4];
const zipFile = process.argv[5];

fs.readFile(zipFile, (err, data) => {

	if (err) {
		console.error(err);
		return;
	}

	const param = {
		FunctionName: name,
		Handler: handler,
		Role: role,
		Runtime: runtime,
		Code: {
			ZipFile: data
		}
	};

	lambda.createFunction(param).promise()
		.then(console.log)
		.catch(console.error);
});
