const protoLoader = require('@grpc/proto-loader')
const grpc = require('@grpc/grpc-js')

const protoFile = '../proto/item/item.proto'

const pd = protoLoader.loadSync(protoFile)
const proto = grpc.loadPackageDefinition(pd)

const client = new proto.item.ItemManage(
    '127.0.0.1:50051',
    grpc.credentials.createInsecure()
)

const stream = client.watch({})

stream.on('data', item => {
    console.log(`*** received: ${JSON.stringify(item)}`)
})

stream.on('end', () => console.log('*** stream end'))
stream.on('error', err => console.log(`*** stream error: ${err}`))
