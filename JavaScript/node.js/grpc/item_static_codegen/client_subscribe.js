
const { ItemSubscribeRequest } = require('./generated/proto/item_pb')
const { ItemManageClient } = require('./generated/proto/item_grpc_pb')

const grpc = require('@grpc/grpc-js')

const client = new ItemManageClient(
    '127.0.0.1:50051',
    grpc.credentials.createInsecure()
)

const stream = client.subscribe(new ItemSubscribeRequest())

stream.on('data', event => {
    console.log(`*** received event: added = ${event.getAdded()}, removed = ${event.getRemoved()}`)
})

stream.on('end', () => console.log('*** stream end'))
stream.on('error', err => console.log(`*** stream error: ${err}`))
