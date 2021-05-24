
import { DynamoDBClient, PutItemCommand } from '@aws-sdk/client-dynamodb'

const tableName = process.env.TABLE_NAME

const config = {}

if (process.env.LOCALSTACK_HOSTNAME) {
    config['endpoint'] = `http://${process.env.LOCALSTACK_HOSTNAME}:${process.env.EDGE_PORT}`
}

const client = new DynamoDBClient(config)

export interface Input {
    id: string
}

export const handler = async (event: Input) => {
    const res = await client.send(new PutItemCommand({
        TableName: tableName,
        Item: {
            id: { S: event.id }
        }
    }))

    console.log(`dynamodb put-item: ${JSON.stringify(res)}`)

    return {
        statusCode: 201,
        body: {
            id: event.id
        }
    }
}
