
const { graphql, buildSchema } = require('graphql')
const { MongoClient, ObjectID } = require('mongodb')

const uri = 'mongodb://localhost'
const dbName = 'items'
const colName = 'data'

const schema = buildSchema(`
    type PageInfo {
        hasPreviousPage: Boolean!
        hasNextPage: Boolean!
    }

    type Item {
        id: ID!
        title: String!
    }

    type ItemEdge {
        node: Item!
        cursor: String!
    }

    type ItemConnection {
        edges: [ItemEdge!]!
        pageInfo: PageInfo!
    }

    type Query {
        items(after: String, before: String, first: Int, last: Int): ItemConnection!
    }
`)

const root = (col) => new Object({
    items: async ({ after, before, first, last }) => {
        const cond = {}

        if (after !== undefined) {
            cond['_id'] = { '$gt': new ObjectID(after) }
        }

        if (before !== undefined) {
            if (!cond['_id']) {
                cond['_id'] = {}
            }

            cond['_id']['$lt'] = new ObjectID(before)
        }

        let cursor = col.find(cond, {_id: 1})

        const pageInfo = {
            hasPreviousPage: false,
            hasNextPage: false
        }

        if (first !== undefined) {
            first = Math.max(0, first)

            const size = await cursor.count(true)

            if (size > first) {
                cursor = cursor.limit(first)
                pageInfo.hasNextPage = true
            }
        }
        else if (before !== undefined) {
            const count = await col.countDocuments(
                {'_id': {'$gte': new ObjectID(before)}}, 
                { limit: 1 }
            )

            pageInfo.hasNextPage = count > 0
        }

        if (last !== undefined) {
            last = Math.max(0, last)

            const size = await cursor.count(true)

            if (size > last) {
                cursor = cursor.skip(size - last).limit(last)
                pageInfo.hasPreviousPage = true
            }
        }
        else if (after !== undefined) {
            const count = await col.countDocuments(
                {'_id': {'$lte': new ObjectID(after)}}, 
                { limit: 1 }
            )

            pageInfo.hasPreviousPage = count > 0
        }

        const ts = await cursor.toArray()

        const edges = ts.map(t => new Object({
            node: { id: t._id, title: t.title },
            cursor: t._id
        }))

        return { edges, pageInfo }
    }
})

const debugItems = async (col, vars) => {
    const q = `
        query FindItems($after: String, $before: String, $first: Int, $last: Int) {
            items(after: $after, before: $before, first: $first, last: $last) {
                edges {
                    node {
                        id
                        title
                    }
                    cursor
                }
                pageInfo {
                    hasPreviousPage
                    hasNextPage
                }
            }
        }
    `

    const r = await graphql(schema, q, root(col), null, vars)
    console.log(JSON.stringify(r))
}

const findId = async (col, title) => {
    const r = await col.findOne({ title }, { _id: 1 })
    return r._id.toString()
}

const run = async (client) => {
    const col = client.db(dbName).collection(colName)

    const id1 = await findId(col, 'item-1')
    const id2 = await findId(col, 'item-2')
    const id3 = await findId(col, 'item-3')
    const id6 = await findId(col, 'item-6')
    const id7 = await findId(col, 'item-7')

    console.log('----- all -----')
    await debugItems(col, {})

    console.log('----- first:2 -----')
    await debugItems(col, {first: 2})

    console.log('----- first: 2, after: "item-3" -----')
    await debugItems(col, {first: 2, after: id3})

    console.log('----- first: 2, after: "item-6" -----')
    await debugItems(col, {first: 2, after: id6})

    console.log('----- first: 2, after: "item-7" -----')
    await debugItems(col, {first: 2, after: id7})

    console.log('----- last: 2 -----')
    await debugItems(col, {last: 2})

    console.log('----- last: 2, before: "item-3" -----')
    await debugItems(col, {last: 2, before: id3})

    console.log('----- last: 2, before: "item-2" -----')
    await debugItems(col, {last: 2, before: id2})

    console.log('----- last: 2, before: "item-1" -----')
    await debugItems(col, {last: 2, before: id1})

    console.log('----- first: 3, last: 2, after: "item-2", before: "item-7" -----')
    await debugItems(col, {first: 3, last: 2, after: id2, before: id7})

    console.log('----- first: 3, last: 5, after: "item-2", before: "item-7" -----')
    await debugItems(col, {first: 3, last: 5, after: id2, before: id7})
}

MongoClient.connect(uri, { useUnifiedTopology: true })
    .then(client => 
        run(client).finally(() => client.close())
    )
    .catch(err => console.error(err))
