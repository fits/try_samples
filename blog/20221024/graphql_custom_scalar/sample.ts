
import { 
    graphql, buildSchema, GraphQLError, ValueNode, Kind 
} from 'https://cdn.skypack.dev/graphql?dts'

const schema = buildSchema(`
    scalar SampleDate

    type Query {
        now: SampleDate!
        nextDay(date: SampleDate!): SampleDate!
    }
`)

const toDate = (v: string) => {
    const d = new Date(v)

    if (isNaN(d.getTime())) {
        throw new GraphQLError('invalid date')
    }

    return d
}

type MaybeObjMap = { [key: string]: unknown } | null | undefined

Object.assign(schema.getTypeMap().SampleDate, {
    serialize: (outputValue: unknown) => {
        console.log(`*** called serialize: ${outputValue}`)

        if (outputValue instanceof Date) {
            return outputValue.toISOString()
        }
        throw new GraphQLError('non Date')
    },
    parseValue: (inputValue: unknown) => {
        console.log(`*** called parseValue: ${inputValue}`)
        
        if (typeof inputValue === 'string') {
            return toDate(inputValue)
        }
        throw new GraphQLError('non string value')
    },
    parseLiteral: (valueNode: ValueNode, variables?: MaybeObjMap) => {
        console.log(`*** called parseLiteral: ${JSON.stringify(valueNode)}, ${JSON.stringify(variables)}`)

        if (valueNode.kind === Kind.STRING) {
            return toDate(valueNode.value)
        }
        throw new GraphQLError('non string value')
    }
})

type DateInput = { date: Date }

const rootValue = {
    now: () => new Date(),
    nextDay: ({ date }: DateInput) => new Date(date.getTime() + 24 * 60 * 60 * 1000)
}

const r1 = await graphql({ schema, rootValue, source: '{ now }' })
console.log(r1)

console.log('-----')

const r2 = await graphql({
    schema, 
    rootValue, 
    source: '{ nextDay(date: "2022-10-21T13:00:00Z") }'
})
console.log(r2)

console.log('-----')

const r3 = await graphql({
    schema,
    rootValue, 
    source: `
        query ($d: SampleDate!) {
            nextDay(date: $d)
        }
    `,
    variableValues: { d: '2022-10-22T14:30:00Z' }
})
console.log(r3)
