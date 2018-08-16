
const Kinesis = require('aws-sdk/clients/kinesis')

const KINESIS_ENDPOINT = process.env.KINESIS_ENDPOINT
const stream = process.argv[2]

const kinesis = new Kinesis({endpoint: KINESIS_ENDPOINT})

kinesis.describeStream({StreamName: stream}).promise()
    .then(d => d.StreamDescription.Shards[0])
    .then(s => {
        const params = {
            StreamName: stream,
            ShardId: s.ShardId,
            ShardIteratorType: 'AFTER_SEQUENCE_NUMBER',
            StartingSequenceNumber: s.SequenceNumberRange.StartingSequenceNumber
        }

        return kinesis.getShardIterator(params).promise()
    })
    .then(i => kinesis.getRecords(i).promise())
    .then(rs => {
        rs.Records.forEach(r => console.log(r.Data.toString()))
    })
    .catch(console.error)
