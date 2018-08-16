
const Kinesis = require('aws-sdk/clients/kinesis')

const KINESIS_ENDPOINT = process.env.KINESIS_ENDPOINT

const stream = process.argv[2]
const key = process.argv[3]
const data = process.argv[4]

const kinesis = new Kinesis({endpoint: KINESIS_ENDPOINT})

const params = {
    StreamName: stream,
    PartitionKey: 'data1',
    Data: data
}

kinesis.putRecord(params).promise()
    .then(res => console.log(res))
    .catch(err => console.error(err))
