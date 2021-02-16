
const { graphql, buildSchema } = require('graphql')

const schema = buildSchema(`
    interface Event {
        id: ID!
    }

    type Created implements Event {
        id: ID!
        title: String!
    }

    type Deleted implements Event {
        id: ID!
        reason: String
    }

    type Query {
        events: [Event!]!
    }
`)

const root = {
    events: () => [
        {id: 'i-1', title: 'sample1'},
        {id: 'i-1'},
        {id: 'i-2', title: 'sample2'},
        {id: 'i-3', title: 'sample3'},
        {id: 'i-3', reason: 'test'},
    ]
}

schema.getType('Event').resolveType = (obj) => 
    obj.hasOwnProperty('title') ? 'Created' : 'Deleted' 

const query = `
{
    events {
        __typename
        id
        ... on Created {
            title
        }
        ... on Deleted {
            reason
        }
    }
}
`

graphql(schema, query, root)
    .then(r => {
        console.log(JSON.stringify(r))
    })
    .catch(err => console.error(err))
