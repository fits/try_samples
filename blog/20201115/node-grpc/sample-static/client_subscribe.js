
const grpc = require('@grpc/grpc-js')

const { ItemSubscribeRequest } = require('./generated/proto/item_pb')
const { ItemManageClient } = require('./generated/proto/item_grpc_pb')

const client = new ItemManageClient(
    '127.0.0.1:50051',
    grpc.credentials.createInsecure()
)

const stream = client.subscribe(new ItemSubscribeRequest())

stream.on('data', event => {
    console.log(`*** received event = ${JSON.stringify(event.toObject())}`)
})

stream.on('end', () => console.log('*** stream end'))
stream.on('error', err => console.log(`*** stream error: ${err}`))
