
const AWS = require('aws-sdk');

AWS.config.loadFromPath('./config.json');

const dynamo = new AWS.DynamoDB({endpoint: 'http://localhost:4567'})

dynamo.listTables().promise().then(console.log);
