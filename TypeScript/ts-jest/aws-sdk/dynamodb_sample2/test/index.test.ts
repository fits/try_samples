
import { DynamoDBClient } from '@aws-sdk/client-dynamodb'
import { handler, SampleInput } from '../src'

jest.mock('@aws-sdk/client-dynamodb')

const DynamoDBClientMock = DynamoDBClient as jest.MockedClass<typeof DynamoDBClient>

beforeEach(() => {
    DynamoDBClientMock.mockClear()
})

test('call handler', async () => {
    const ev: SampleInput = { id: 'a1' }

    const res = await handler(ev)

    expect(res.statusCode).toBe(0)

    expect(DynamoDBClientMock).toBeCalled()
    expect(DynamoDBClientMock.mock.instances).toHaveLength(1)
})

test('call handler with dynamodb error', async () => {
    const ev: SampleInput = { id: 'a1' }

    jest.spyOn(DynamoDBClientMock.prototype, 'send')
        .mockRejectedValueOnce(new Error('test error') as never)

    const res = await handler(ev)

    expect(res.statusCode).toBe(1)

    expect(DynamoDBClientMock).toBeCalled()
    expect(DynamoDBClientMock.mock.instances).toHaveLength(1)
})