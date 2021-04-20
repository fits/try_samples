
import { DynamoDBClient, PutItemCommand } from '@aws-sdk/client-dynamodb'
import { handler, SampleInput } from '../src'

jest.mock('@aws-sdk/client-dynamodb')

const DynamoDBClientMock = 
    DynamoDBClient as jest.MockedClass<typeof DynamoDBClient>

const PutItemCommandMock = 
    PutItemCommand as jest.MockedClass<typeof PutItemCommand>

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

test('call handler2', async () => {
    const ev: SampleInput = { id: 'a1' }

    DynamoDBClientMock.prototype.send.mockResolvedValueOnce(
        { Attributes: {} } as never
    )

    const res = await handler(ev)

    expect(res.statusCode).toBe(0)

    expect(PutItemCommandMock).toBeCalled()
    expect(PutItemCommandMock.mock.calls[0][0].Item).toEqual({id: {S: 'a1'}})

    expect(DynamoDBClientMock).toBeCalled()
    expect(DynamoDBClientMock.mock.instances).toHaveLength(1)
})

test('call handler with dynamodb error1', async () => {
    const ev: SampleInput = { id: 'a1' }

    DynamoDBClientMock.prototype.send.mockRejectedValueOnce(
        new Error('test error') as never
    )

    const res = await handler(ev)

    expect(res.statusCode).toBe(1)

    expect(DynamoDBClientMock).toBeCalled()
    expect(DynamoDBClientMock.mock.instances).toHaveLength(1)
})

test('call handler with dynamodb error2', async () => {
    const ev: SampleInput = { id: 'a1' }

    jest.spyOn(DynamoDBClientMock.prototype, 'send')
        .mockRejectedValueOnce(new Error('test error') as never)

    const res = await handler(ev)

    expect(res.statusCode).toBe(1)

    expect(DynamoDBClientMock).toBeCalled()
    expect(DynamoDBClientMock.mock.instances).toHaveLength(1)
})