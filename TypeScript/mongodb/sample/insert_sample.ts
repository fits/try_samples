import { MongoClient } from 'mongodb'

const url = 'mongodb://localhost/sample'
const colName = 'data'

const run = async (client: MongoClient) => {
    const col = client.db().collection(colName)

    const res = await col.insertOne({name: `sample-${Date.now()}`})

    console.log(`id: ${res.insertedId}, count: ${res.insertedCount}`)
}

MongoClient.connect(url, { useUnifiedTopology: true })
    .then(client => 
        run(client).finally(() => client.close())
    )
    .catch(err => console.error(err))
