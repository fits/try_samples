
import { 
    graphql, buildSchema, GraphQLError, ValueNode, Kind 
} from 'https://cdn.skypack.dev/graphql?dts'

const schema = buildSchema(`
    scalar Date

    type Task {
        action: String!
        deadline: Date
    }

    input TaskInput {
        action: String!
        deadline: Date     
    }

    type Query {
        find(action: String!): Task!
    }

    type Mutation {
        create(input: TaskInput!): Task!
    }
`)

type TaskInput = { action: string }
type TaskCreateInput = { input: { action: string, deadline: Date } }
type Task = { action: string, deadline: Date }

const rootValue = {
    find: ({ action }: TaskInput) => {
        return { action, deadline: new Date() }
    },
    create: ({ input }: TaskCreateInput) => {
        return { action: input.action, deadline: input.deadline } as Task
    }
}

Object.assign(schema.getTypeMap().Date, {
    serialize: (outputValue: Date) => {
        console.log(`*** called serialize: ${outputValue}`)
        return outputValue.toISOString()
    },
    parseValue: (inputValue: unknown) => {
        console.log(`*** called parseValue: ${inputValue}`)

        if (typeof inputValue === 'string') {
            return new Date(inputValue)
        }

        throw new GraphQLError('non string value')
    },
    parseLiteral: (valueNode: ValueNode) => {
        console.log(`*** called parseLiteral: ${JSON.stringify(valueNode)}`)

        if (valueNode.kind === Kind.STRING) {
            return new Date(valueNode.value)
        }

        throw new GraphQLError('non string value')
    }
})

const r1 = await graphql({ schema, source: '{ find(action: "test1") { action deadline } }', rootValue })
console.log(r1)

const s2 = `
    mutation {
        create(input: { action: "test2", deadline: "2022-10-14T13:00:00Z" }) {
            action
            deadline
        }
    }
`

const r2 = await graphql({ schema, source: s2, rootValue })
console.log(r2)
