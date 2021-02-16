
const { graphql } = require('graphql')
const { makeExecutableSchema } = require('@graphql-tools/schema')

const typeDefs = `
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
`

const resolvers = {
    Event: {
        __resolveType: (obj, ctx, info) => 
            obj.hasOwnProperty('title') ? 'Created' : 'Deleted' 
    },
    Query: {
        events: () => [
            {id: 'i-1', title: 'sample1'},
            {id: 'i-1'},
            {id: 'i-2', title: 'sample2'},
            {id: 'i-3', title: 'sample3'},
            {id: 'i-3', reason: 'test'},
        ]
    }
}

const schema = makeExecutableSchema({ typeDefs, resolvers })

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

graphql(schema, query)
    .then(r => {
        console.log(JSON.stringify(r))
    })
    .catch(err => console.error(err))
