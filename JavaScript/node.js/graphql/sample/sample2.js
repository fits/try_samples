const { graphql, buildSchema } = require('graphql')

const schema = buildSchema(`
type Item {
    name: String!
    value: Int!
}

type Query {
    items: [Item]!
    find(value: Int): [Item]
}
`)

const d = {
    items: [
        {
            name: () => 'data1',
            value: () => 10
        },
        {
            name: () => 'data2',
            value: () => 5
        },
        {
            name: () => 'data3',
            value: () => 1
        }
    ],
    find: (c) => d.items.filter(i => i.value() == c.value)
}

const q = v => `{
    find(value: ${v}) {
        name
    }
}`

graphql(schema, q(10), d)
    .then(res => console.log(JSON.stringify(res)))
    .then(a => graphql(schema, q(1), d))
    .then(res => console.log(JSON.stringify(res)))
    .catch(console.error)
