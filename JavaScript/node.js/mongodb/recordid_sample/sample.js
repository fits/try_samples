
const { MongoClient } = require('mongodb')

const uri = 'mongodb://localhost'
const dbName = 'items'
const colName = 'data'

const run = async (client) => {
    const col = client.db(dbName).collection(colName)

    const rs = await col.find().showRecordId(true).toArray()
    console.log(rs)

    console.log('-----')

    const cursor = col.find({value: { '$gte': 5 }}).showRecordId(true)

    while (await cursor.hasNext()) {
        console.log(await cursor.next())
    }
}

MongoClient.connect(uri, { useUnifiedTopology: true })
    .then(client => 
        run(client).finally(() => client.close())
    )
    .catch(err => console.error(err))
