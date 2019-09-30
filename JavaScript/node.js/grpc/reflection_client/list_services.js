
const grpc = require('grpc')
const protoLoader = require('@grpc/proto-loader')

const pd = protoLoader.loadSync('reflection.proto', {
    keepCase: true,
    defaults: true
})

const proto = grpc.loadPackageDefinition(pd).grpc.reflection.v1alpha

const client = new proto.ServerReflection(
    '127.0.0.1:50051', 
    grpc.credentials.createInsecure()
)

const call = client.ServerReflectionInfo()

call.on('error', err => {
    call.end()
    console.error(err)
})

call.on('data', res => {
    call.end()
    
    res.list_services_response.service.forEach(s => {
        console.log(s.name)
    })
})

call.write({host: 'localhost', list_services: ''})
