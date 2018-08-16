
const Kinesis = require('aws-sdk/clients/kinesis')

const KINESIS_ENDPOINT = process.env.KINESIS_ENDPOINT
const stream = process.argv[2]

const kinesis = new Kinesis({endpoint: KINESIS_ENDPOINT})

const params = {
    StreamName: stream,
    ShardCount: 1
}

kinesis.createStream(params).promise()
    .then(res => console.log(res))
    .catch(err => console.error(err))
