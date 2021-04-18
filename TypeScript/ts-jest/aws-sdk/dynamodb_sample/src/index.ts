
import { Context } from 'aws-lambda'
import { DynamoDBClient, PutItemCommand } from '@aws-sdk/client-dynamodb'

export interface SampleInput {
    readonly id: string
}

export interface SampleOutput {
    readonly statusCode: number
}

type Handler = (event: SampleInput, context?: Context) => Promise<SampleOutput>

const dynamodb = new DynamoDBClient({})

export const handler: Handler = async (event) => {
    console.log(`*** handle: id=${event.id}`)

    await dynamodb.send(new PutItemCommand({
        TableName: 'Events',
        Item: {
            id: { S: event.id }
        }
    }))

    return {
        statusCode: 0
    }
}