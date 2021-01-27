
const { GraphQLAPIClass } = require('@aws-amplify/api-graphql')

const id = process.argv[2]

const api = new GraphQLAPIClass(null)

api.configure({
    API: {
        graphql_endpoint: 'http://localhost:4000/'
    }
})

const query = `
    {
        find(id: "${id}") {
            id
            value
        }
    }
`

api.graphql({ query })
    .then(r => console.log(r))
    .catch(err => console.error(err))
