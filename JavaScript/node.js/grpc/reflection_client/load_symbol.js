
const grpc = require('grpc')
const protoLoader = require('@grpc/proto-loader')
const descriptor = require('protobufjs/ext/descriptor')

const serviceName = process.argv[2]

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
    
    res.file_descriptor_response.file_descriptor_proto
        .map(buf => descriptor.FileDescriptorProto.decode(buf))
        .forEach(d => {
            console.log(JSON.stringify(d, null, 2))
        })
})

call.write({host: 'localhost', file_containing_symbol: serviceName})
