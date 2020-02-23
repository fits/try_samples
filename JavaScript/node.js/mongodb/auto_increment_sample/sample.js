
const mongoClient = require('mongodb').MongoClient

const mongoUrl = 'mongodb://localhost/sample'
const colName = 'data'
const seqId = 'data_seq_no'

const run = async () => {
    const client = await mongoClient.connect(mongoUrl, {
        useUnifiedTopology: true
    })

    const seqCol = client.db().collection(`${colName}_seq`)

    const res = await seqCol.findOneAndUpdate(
        {_id: seqId},
        {'$inc': { seq_no: 1 }},
        { upsert: true, returnOriginal: false}
    )

    const seqNo = res.value.seq_no

    const col = client.db().collection(colName)

    const res2 = await col.insertOne({
        _id: seqNo,
        item: `item-${seqNo}`
    })

    console.log(res2.result)

    client.close()
}

run().catch(err => console.error(err))

