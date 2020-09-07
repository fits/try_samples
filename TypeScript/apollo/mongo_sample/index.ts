
import { ApolloServer, gql } from 'apollo-server'
import { MongoClient, ObjectId } from 'mongodb'

const mongoUrl = 'mongodb://localhost'
const dbName = 'items'
const colName = 'data'

type ItemId = string
type Category = 'Standard' | 'Extra'

interface Item {
    id: ItemId
    category: Category
    value: number
}

interface Store {
    load(id: ItemId): Promise<Item | undefined>
    create(category: Category, value: number): Promise<Item>
    update(id: ItemId, value: number): Promise<Item | undefined>
}

const typeDefs = gql`
    enum Category {
        Standard
        Extra
    }

    input CreateItem {
        category: Category!
        value: Int!
    }

    input UpdateItemValue {
        id: ID!
        value: Int!
    }

    input ItemCommand {
        create: CreateItem
        updateValue: UpdateItemValue
    }

    type Item {
        id: ID!
        category: Category!
        value: Int!
    }

    type Mutation {
        action(input: ItemCommand!): Item
    }

    type Query {
        find(id: ID!): Item
    }
`

const resolvers = {
    Query: {
        find: async (parent, { id }, { store }, info) => {
            console.log(`*** call find: ${id}`)

            return store.load(id)
        }
    },
    Mutation: {
        action: async (parent, { input }, { store }, info) => {
            console.log(`*** call action: ${JSON.stringify(input)}`)

            if (input.create) {
                return store.create(input.create.category, input.create.value)
            }
            else if (input.updateValue) {
                return store.update(input.updateValue.id, input.updateValue.value)
            }

            return undefined
        }
    }
}

const run = async () => {
    const client = await MongoClient.connect(mongoUrl, { useUnifiedTopology: true })
    const col = client.db(dbName).collection(colName)

    const store = {
        load: async (id: ItemId) => {
            const res = await col.findOne({ _id: new ObjectId(id) })

            if (res) {
                return { id: res._id.valueOf(), ...res }
            }

            return undefined
        },
        create: async (category: Category, value: number) => {
            const item = { category, value }
            const res = await col.insertOne(item)

            return { id: res.insertedId.valueOf(), ...item }
        },
        update: async (id: ItemId, value: number) => {
            const res = await col.updateOne(
                { _id: new ObjectId(id) },
                { '$set': { value } }
            )

            if (res.modifiedCount) {
                return store.load(id)
            }

            return undefined
        }
    } as Store

    const server = new ApolloServer({
        typeDefs,
        resolvers,
        context: {
            store
        }
    })

    const res = await server.listen()
    console.log(res.url)
}

run().catch(err => console.error(err))
