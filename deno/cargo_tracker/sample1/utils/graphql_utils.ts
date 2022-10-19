
import {
    graphql, buildSchema, GraphQLScalarType, GraphQLError, Kind
} from 'https://cdn.skypack.dev/graphql?dts'

type StrMap = { [s: string]: unknown }

export const bindGql = (schemaSrc: string, rootValue: unknown, typeResolvs: StrMap = {}) => {
    const schema = buildSchema(schemaSrc)

    Object.entries(typeResolvs).forEach(([k, v]) => {
        Object.assign(
            schema.getTypeMap()[k] ?? {},
            v
        )
    })

    return (source: string) => graphql({ schema, source, rootValue })
}

export const GraphQLDate = new GraphQLScalarType<Date, string>({
    name: 'Date',
    serialize: (outputValue) => {
        if (outputValue instanceof Date) {
            return outputValue.toISOString()
        }
        throw error('non date')
    },
    parseValue: (inputValue) => {    
        if (typeof inputValue === 'string') {
            return toDate(inputValue)
        }
        throw error('non string value')
    },
    parseLiteral: (valueNode) => {
        if (valueNode.kind === Kind.STRING) {
            return toDate(valueNode.value)
        }
        throw error('non string value')
    }
})

export const error = (msg: string) => new GraphQLError(msg)

const toDate = (v: string) => {
    const d = new Date(v)

    if (isNaN(d.getTime())) {
        throw new GraphQLError('invalid date')
    }

    return d
}