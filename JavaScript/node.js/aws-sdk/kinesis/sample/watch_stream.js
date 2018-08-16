
const Kinesis = require('aws-sdk/clients/kinesis')
const LocalStorage = require('node-localstorage').LocalStorage

const SLEEP_TIME = 5000

const STORAGE_KEY = 'seqNo'
const STORAGE_FILE = './storage'

const KINESIS_ENDPOINT = process.env.KINESIS_ENDPOINT
const stream = process.argv[2]

const kinesis = new Kinesis({endpoint: KINESIS_ENDPOINT})
const storage = new LocalStorage(STORAGE_FILE)

const loadSeqNo = defaultSeqNo => {
    const res = storage.getItem(STORAGE_KEY)
    return res ? res : defaultSeqNo
}

const saveSeqNo = seqNo => storage.setItem(STORAGE_KEY, seqNo)

const processData = data => console.log(data)

async function watch(shardId, defaultSeqNo) {
    const params = {
        StreamName: stream,
        ShardId: shardId,
        ShardIteratorType: 'AFTER_SEQUENCE_NUMBER',
        StartingSequenceNumber: loadSeqNo(defaultSeqNo)
    }

    const rs = await kinesis.getShardIterator(params).promise()
                            .then(i => kinesis.getRecords(i).promise())
                            .then(r => r.Records)

    await rs.reduce(
        (acc, r) =>
            acc.then(v => processData(r.Data.toString()))
                .then(v => saveSeqNo(r.SequenceNumber))
        ,
        Promise.resolve('')
    ).catch(err => console.error(err))

    setTimeout(() => watch(shardId, defaultSeqNo), SLEEP_TIME)
}

kinesis.describeStream({StreamName: stream}).promise()
    .then(d => d.StreamDescription.Shards[0])
    .then(s => [s.ShardId, s.SequenceNumberRange.StartingSequenceNumber])
    .then( ([shardId, defaultSeqNo]) =>
        watch(shardId, defaultSeqNo)
    )
