const grpc = require('grpc')
const protoLoader = require('@grpc/proto-loader')

const address = '127.0.0.1:50051'
const clientId = process.argv[2]
const durableName = process.argv[3]

const pd = protoLoader.loadSync('../service.proto', {
    keepCase: true,
    defaults: true
})

const proto = grpc.loadPackageDefinition(pd).sample

const client = new proto.EventNotifyService(
    address, 
    grpc.credentials.createInsecure()
)

const call = client.subscribe({client_id: clientId, durable_name: durableName})

call.on('data', d => {
    console.log(d)
})

call.on('end', () => {
    console.log('end')
})

call.on('error', (err) => {
    console.error(err)
})
