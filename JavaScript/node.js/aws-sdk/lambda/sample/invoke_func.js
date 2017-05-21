
const AWS = require('aws-sdk');

AWS.config.loadFromPath('./config.json');

const lambda = new AWS.Lambda();

const name = process.argv[2];

const param = {
	FunctionName: name
};

lambda.invoke(param).promise()
	.then(console.log)
	.catch(console.error);
