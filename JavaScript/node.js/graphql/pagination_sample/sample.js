
const { graphql, buildSchema } = require('graphql')

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

const items = [
    {id: 'id-1', title: 'item-1'},
    {id: 'id-2', title: 'item-2'},
    {id: 'id-3', title: 'item-3'},
    {id: 'id-4', title: 'item-4'},
    {id: 'id-5', title: 'item-5'},
    {id: 'id-6', title: 'item-6'},
    {id: 'id-7', title: 'item-7'},
    {id: 'id-8', title: 'item-8'},
]

const root = {
    items: ({ after, before, first, last }) => {
        let ts = Array.from(items)

        let hasPreviousPage = false
        let hasNextPage = false

        if (after !== undefined) {
            const offset = ts.findIndex(t => t.id == after)

            hasPreviousPage = offset > 0
            ts = offset >= 0 ? ts.slice(offset + 1) : []
        }

        if (before !== undefined) {
            const offset = ts.findIndex(t => t.id == before)

            hasNextPage = offset >= 0 && (offset + 1) < ts.length
            ts = offset >= 0 ? ts.slice(0, offset) : []
        }

        if (first !== undefined) {
            first = Math.max(0, first)

            hasNextPage = ts.length > first
            ts = ts.slice(0, first)
        }

        if (last !== undefined) {
            last = Math.max(0, last)

            hasPreviousPage = ts.length > last
            ts = ts.slice(Math.max(0, ts.length - last))
        }

        const edges = ts.map(t => new Object({
            node: t,
            cursor: t.id
        }))

        return { edges, pageInfo: { hasPreviousPage, hasNextPage } }
    }
}

const debugItems = async (vars) => {
    const q = `
        query FindItems($before: String, $after: String, $first: Int, $last: Int) {
            items(before: $before, after: $after, first: $first, last: $last) {
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

    const r = await graphql(schema, q, root, null, vars)
    console.log(JSON.stringify(r))
}

const run = async () => {
    console.log('----- all -----')
    await debugItems({})

    console.log('----- first:2 -----')
    await debugItems({first: 2})

    console.log('----- first: 2, after: "id-3" -----')
    await debugItems({first: 2, after: "id-3"})

    console.log('----- first: 2, after: "id-6" -----')
    await debugItems({first: 2, after: "id-6"})

    console.log('----- first: 2, after: "id-7" -----')
    await debugItems({first: 2, after: "id-7"})

    console.log('----- first: 2, after: "id-100" -----')
    await debugItems({first: 2, after: "id-100"})

    console.log('----- last: 2 -----')
    await debugItems({last: 2})

    console.log('----- last: 2, before: "id-3" -----')
    await debugItems({last: 2, before: "id-3"})

    console.log('----- last: 2, before: "id-2" -----')
    await debugItems({last: 2, before: "id-2"})

    console.log('----- last: 2, before: "id-1" -----')
    await debugItems({last: 2, before: "id-1"})

    console.log('----- first: 3, last: 2, after: "id-2", before: "id-7" -----')
    await debugItems({first: 3, last: 2, after: "id-2", before: "id-7"})

    console.log('----- first: 3, last: 5, after: "id-2", before: "id-7" -----')
    await debugItems({first: 3, last: 5, after: "id-2", before: "id-7"})
}

run().catch(err => console.error(err))
