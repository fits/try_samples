
const {
    DynamoDBStreamsClient,
    ListStreamsCommand,
    DescribeStreamCommand,
    GetShardIteratorCommand,
    GetRecordsCommand
} = require('@aws-sdk/client-dynamodb-streams')

const config = require('./config')

const item = require('./item')

const eventTable = 'ItemEvents'

const threshold = process.env['SNAPSHOT_THRESHOLD'] ? 
    parseInt(process.env['SNAPSHOT_THRESHOLD']) : 5

const seqNo = process.argv[2]

const client = new DynamoDBStreamsClient(config)

const run = async () => {
    const ls = await client.send(new ListStreamsCommand({
        TableName: eventTable
    }))

    const streamArn = ls.Streams[0].StreamArn

    const ds = await client.send(new DescribeStreamCommand({
        StreamArn: streamArn
    }))

    const shardId = ds.StreamDescription.Shards[0].ShardId

    const siType = seqNo ? 'AFTER_SEQUENCE_NUMBER' : 'AT_SEQUENCE_NUMBER'
    const siSeqNo = seqNo ? seqNo : ds.StreamDescription.Shards[0].SequenceNumberRange.StartingSequenceNumber

    const gs = await client.send(new GetShardIteratorCommand({
        StreamArn: streamArn,
        ShardId: shardId,
        ShardIteratorType: siType,
        SequenceNumber: siSeqNo
    }))

    const shardIterator = gs.ShardIterator

    const gr = await client.send(new GetRecordsCommand({
        ShardIterator: shardIterator
    }))

    gr.Records.forEach(async (r) => {
        const it = r.dynamodb.NewImage
        const rev = parseInt(it.Rev.N)

        if (rev % threshold == 0) {
            const itemId = it.ItemId.S

            console.log(`*** snapshot: ${itemId}`)

            try {
                await item.snapshot(itemId)
            } catch(e) {
                console.error(e)
            }
        }

        console.log(`*** seqno: ${r.dynamodb.SequenceNumber}`)
    })
}

run().catch(err => console.error(err))
