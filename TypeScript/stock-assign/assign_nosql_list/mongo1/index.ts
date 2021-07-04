
import { MongoClient } from 'mongodb'

const host = process.env.MONGO_HOST ?? 'localhost'
const port = 27017

const endpoint = `mongodb://${host}:${port}`
const dbName = 'stock_assign'
const colName = 'stocks'

const client = new MongoClient(endpoint, { useUnifiedTopology: true })

export const init = async (id, qty) => {
    await client.connect()

    const db = client.db(dbName)
    const col = db.collection(colName)

    await col.updateOne(
        { _id: id }, 
        {
            '$set': {
                qty,
                assigns: []
            }
        }, 
        { upsert: true }
    )
}

export const assign = async (id, assignId, q) => {
    const ds = Array(q).fill(assignId)

    const db = client.db(dbName)
    const col = db.collection(colName)

    const r = await col.updateOne(
        {
            _id: id, 
            $where: `this.qty - this.assigns.length >= ${q}`
        },
        {
            '$push': {
                assigns: { '$each': ds }
            }
        }
    )

    if (r.modifiedCount < 1) {
        throw new Error('assign failed')
    }
}
