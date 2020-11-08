
const protoLoader = require('@grpc/proto-loader')
const grpc = require('@grpc/grpc-js')

const protoFile = './proto/item.proto'
const opts = {}

const pd = protoLoader.loadSync(protoFile, opts)
const proto = grpc.loadPackageDefinition(pd)

const client = new proto.item.ItemManage(
    '127.0.0.1:50051',
    grpc.credentials.createInsecure()
)

const stream = client.Subscribe({})

stream.on('data', event => {
    console.log(`*** received event = ${JSON.stringify(event)}`)
})

stream.on('end', () => console.log('*** stream end'))
stream.on('error', err => console.log(`*** stream error: ${err}`))
