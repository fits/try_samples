const protoLoader = require('@grpc/proto-loader')
const grpc = require('@grpc/grpc-js')

const protoFile = '../proto/item/item.proto'

const value = parseInt(process.argv[2])

const pd = protoLoader.loadSync(protoFile)
const proto = grpc.loadPackageDefinition(pd)

const client = new proto.item.ItemManage(
    '127.0.0.1:50051',
    grpc.credentials.createInsecure()
)

client.create({ value }, (err, res) => {
    if (err) {
        console.error(err)
        return
    }

    console.log(res)
})
