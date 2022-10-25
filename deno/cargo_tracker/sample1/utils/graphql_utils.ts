
import {
    graphql, buildSchema, GraphQLScalarType, GraphQLError, Kind
} from 'https://cdn.skypack.dev/graphql?dts'

import { Maybe, ObjMap } from './type_utils.ts'

export const bindGql = (schemaSrc: string, rootValue: unknown, typeResolvs: ObjMap = {}) => {
    const schema = buildSchema(schemaSrc)

    Object.entries(typeResolvs).forEach(([k, v]) => {
        Object.assign(
            schema.getTypeMap()[k] ?? {},
            v
        )
    })

    return (source: string, variableValues?: Maybe<ObjMap>, operationName?: Maybe<string>) => 
        graphql({ schema, source, rootValue, variableValues, operationName })
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

export type ShutdownHook = () => Promise<void>

export type GqlServeInput = {
    schema: string,
    rootValue: unknown,
    port?: Maybe<number>, 
    typeResolvs?: Maybe<ObjMap>,
    shutdownHook?: Maybe<ShutdownHook>
}

export const gqlServe = async (input: GqlServeInput) => {

    const port = input.port ?? 8080
    const typeResolvs = input.typeResolvs ?? {}
    const shutdownHook = input.shutdownHook ?? (async () => {})

    const gqlFunc = bindGql(input.schema, input.rootValue, typeResolvs)

    const proc = async (event: Deno.RequestEvent) => {
        switch (event.request.method) {
            case 'POST':
                if (event.request.headers.get('content-type') === 'application/json') {
                    try {
                        const params = await event.request.json()

                        await respond(
                            event,
                            gqlFunc(
                                params.query,
                                params.variables,
                                params.operationName
                            )
                        )
                    } catch (e) {
                        await errorRespond(event, e.message)
                    }
                }
                else {
                    const source = await event.request.text()
                    await respond(event, gqlFunc(source))
                }
                break
            default:
                await errorRespond(
                    event, 
                    `not supported http method: ${event.request.method}`, 
                    405
                )
        }
    }

    await serve(proc, port, shutdownHook)
}

const respond = async (event: Deno.RequestEvent, result: Promise<unknown>) => {
    await event.respondWith(new Response(JSON.stringify(await result)))
}

const errorRespond = async (event: Deno.RequestEvent, errMsg = '', status = 400) => {
    await event.respondWith(new Response(errMsg, { status }))
}

const toDate = (v: string) => {
    const d = new Date(v)

    if (isNaN(d.getTime())) {
        throw new GraphQLError('invalid date')
    }

    return d
}

type Processor = (event: Deno.RequestEvent) => Promise<void>

const serve = async (process: Processor, port = 8080, 
    shutdownHook: ShutdownHook = async () => {}) => {
    
    const handler = async (con: Deno.Conn) => {
        for await (const req of Deno.serveHttp(con)) {
            await process(req)
        }
    }
    
    const server = Deno.listen({ port })

    const terminate = () => {
        shutdownHook().finally(() => {
            try {
                server.close()
            } catch(e) {
                console.error(e)
            }
        })
    }

    Deno.addSignalListener('SIGINT', terminate)
    Deno.addSignalListener('SIGTERM', terminate)
    
    for await (const con of server) {
        handler(con)
    }
}
