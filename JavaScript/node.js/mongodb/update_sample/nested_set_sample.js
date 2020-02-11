
const url = 'mongodb://localhost:27017'
const dbName = 'sample'
const colName = 'tasks'

const MongoClient = require('mongodb').MongoClient(url, {
    useUnifiedTopology: true
})

const run = async () => {
    const client = await MongoClient.connect()

    const db = client.db(dbName)
    const col = db.collection(colName)

    const res1 = await col.insertOne({
        name: 'task2',
        details: [
        ]
    })

    const id = res1.insertedId

    console.log(id)

    await col.updateOne(
        { _id: id },
        {
            '$push': {
                details: {process_id: 'p01', status: 'ready'} 
            }
        }
    )

    await col.updateOne(
        { _id: id },
        {
            '$push': {
                details: {
                    '$each': [
                        {process_id: 'p02', status: 'ready'},
                        {process_id: 'p03', status: 'ready'}
                    ]
                }
            }
        }
    )

    await col.updateOne(
        { _id: id },
        {
            '$set': { 'details.2.status': 'error' }
        }
    )

    await col.updateOne(
        { _id: id, 'details.process_id': 'p02' },
        {
            '$set': { 'details.$.status': 'done' }
        }
    )

    client.close()
}

run().catch(err => console.error(err))
