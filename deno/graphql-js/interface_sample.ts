
import { graphql, buildSchema } from 'https://cdn.skypack.dev/graphql?dts'

const schema = buildSchema(`
    interface Value {
        value: Int!
    }

    type LowValue implements Value {
        value: Int!
    }

    type HighValue implements Value {
        value: Int!
    }

    type Query {
        value(v: Int!): Value!
    }
`)

type ValueInput = { v: number }

type LowValue = { tag: 'LowValue', value: number }
type HighValue = { tag: 'HighValue', value: number }
type Value = LowValue | HighValue

const rootValue = {
    value: ({ v }: ValueInput) => {
        if (v <= 50) {
            return { tag: 'LowValue', value: v } as LowValue
        }

        return { tag: 'HighValue', value: v } as HighValue
    }
}

const typeResolver = (value: Value) => value.tag

const runGraphQL = (source: string) => graphql({schema, source, rootValue, typeResolver})

const r1 = await runGraphQL(`
    {
        value(v: 12) {
            __typename
            value
        }
    }
`)

console.log(r1)

const r2 = await runGraphQL(`
    {
        value(v: 56) {
            __typename
            value
        }
    }
`)

console.log(r2)
