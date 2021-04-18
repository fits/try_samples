
import { DynamoDBClient } from '@aws-sdk/client-dynamodb'
import { handler, SampleInput } from '../src'

test('call handler', async () => {
    const ev: SampleInput = { id: 'a1' }

    const sendSpy = jest.spyOn(DynamoDBClient.prototype, 'send')
        .mockImplementationOnce( cmd => {
            expect(cmd.input['TableName']).toBe('Events')
            expect(cmd.input['Item']).toEqual( {id: {S: 'a1'}} )

            return { Arributes: {} }
        })

    const res = await handler(ev)

    expect(res.statusCode).toBe(0)
    expect(sendSpy).toBeCalledTimes(1)
})
