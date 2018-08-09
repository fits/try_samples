const { graphql, buildSchema } = require('graphql')

const schema = buildSchema('type Query { name: String, value: Int }')

const d = {
    name: () => 'data1',
    value: () => 10
}

graphql(schema, '{ name }', d)
    .then(res => console.log(res))
    .then(a => graphql(schema, '{ name, value }', d))
    .then(res => console.log(res))
    .catch(console.error)
