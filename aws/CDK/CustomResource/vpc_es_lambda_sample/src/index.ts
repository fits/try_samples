
import { CloudFormationCustomResourceEvent as Event, Context } from 'aws-lambda'
import fetch from 'node-fetch'

import { Client } from '@elastic/elasticsearch'
import { createAWSConnection, awsGetCredentials } from '@acuris/aws-es-connection'

const endpoint = process.env.ENDPOINT
const node = `http://${endpoint}`
const index = 'items'

type Status = 'SUCCESS' | 'FAILED'

export const handler = async (event: Event, ctx: Context) => {
    console.log(event)

    try {
        if (event.RequestType == 'Delete') {
            await send(event, ctx, 'SUCCESS')
        }
        else {
            try {
                await initSchema()
                await send(event, ctx, 'SUCCESS')
            } catch(err) {
                console.error(err)
                await send(event, ctx, 'FAILED', err)
            }
        }
    } catch(err) {
        console.error(err)
    }
}

const initSchema = async () => {
    const cred = await awsGetCredentials()
    const AWSConnection = createAWSConnection(cred)

    const client = new Client({ 
        node,
        ...AWSConnection,
        maxRetries: 1
    })

    const { body: exists } = await client.indices.exists({ index })

    if (!exists) {
        const { body } = await client.indices.create({
            index,
            body: {
                mappings: {
                    properties: {
                        code: { type: 'keyword' },
                        name: { type: 'text', analyzer: 'kuromoji' },
                        colors: {
                            type: 'nested',
                            properties: {
                                color: { type: 'keyword' },
                                price: {
                                    type: 'scaled_float',
                                    scaling_factor: 100
                                }
                            }
                        }
                    }
                }
            }
        })

        console.log(body)
    }
}

const send = async (event: Event, ctx: Context, status: Status, data = {}) => {
    console.log('send response to s3')

    const responseBody = {
        Status: status,
        Reason: '',
        PhysicalResourceId: ctx.logStreamName,
        StackId: event.StackId,
        RequestId: event.RequestId,
        LogicalResourceId: event.LogicalResourceId,
        Data: data
    }

    const res = await fetch(event.ResponseURL, {
        method: 'PUT',
        body: JSON.stringify(responseBody)
    })

    console.log(`send response: status code=${res.status}, message=${res.statusText}`)
}
