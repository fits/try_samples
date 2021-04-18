
import { Context } from 'aws-lambda'
import { DynamoDBClient, PutItemCommand } from '@aws-sdk/client-dynamodb'

export interface SampleInput {
    readonly id: string
}

export interface SampleOutput {
    readonly statusCode: number
}

type Handler = (event: SampleInput, context?: Context) => Promise<SampleOutput>

export const handler: Handler = async (event) => {
    console.log(`*** handle: id=${event.id}`)

    const dynamodb = new DynamoDBClient({})

    try {
        await dynamodb.send(new PutItemCommand({
            TableName: 'Events',
            Item: {
                id: { S: event.id }
            }
        }))
    } catch(e) {
        console.log(e.message)
        return { statusCode: 1 }
    }

    return { statusCode: 0 }
}