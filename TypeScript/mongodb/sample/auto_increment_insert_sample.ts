import { MongoClient } from 'mongodb'

const url = 'mongodb://localhost/sample'
const colName = 'data'
const seqId = 'data_seq'

const run = async (client: MongoClient) => {
    const seqCol = client.db().collection(`${colName}_seq`)

    const seqRes = seqCol.findOneAndUpdate(
        { _id: seqId },
        { '$inc': { seq_no: 1 }},
        { upsert: true, returnOriginal: false }
    )

    const seqNo = (await seqRes).value.seq_no

    const col = client.db().collection(colName)

    const res = await col.insertOne({
        _id: seqNo,
        name: `sample-${Date.now()}`
    })

    console.log(`id: ${res.insertedId}, count: ${res.insertedCount}`)
}

MongoClient.connect(url, { useUnifiedTopology: true })
    .then(client => 
        run(client).finally(() => client.close())
    )
    .catch(err => console.error(err))
