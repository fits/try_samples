
import { MongoClient, ObjectId } from 'https://deno.land/x/mongo@v0.31.1/mod.ts'

const client = new MongoClient()

type Item = { _id: ObjectId, name: string, value: number, date: Date }

await client.connect('mongodb://127.0.0.1')

const db = client.database('sample1')
const items = db.collection<Item>('items')

const id1 = await items.insertOne({ name: 'item-1', value: 1, date: new Date() })
console.log(id1)

const id2 = await items.insertOne({ name: 'item-2', value: 2, date: new Date() })
console.log(id2)

for await (const it of items.find()) {
    console.log(it)
}

client.close()
