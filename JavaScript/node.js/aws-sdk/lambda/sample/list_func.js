
const AWS = require('aws-sdk');

AWS.config.loadFromPath('./config.json');

const lambda = new AWS.Lambda();

lambda.listFunctions().promise().then(console.log);
