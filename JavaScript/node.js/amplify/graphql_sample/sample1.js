
const { API } = require('@aws-amplify/api')

const id = process.argv[2]

API.configure({
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

API.graphql({ query })
    .then(r => console.log(r))
    .catch(err => console.error(err))
