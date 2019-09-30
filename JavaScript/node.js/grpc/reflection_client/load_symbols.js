
const grpc = require('grpc')
const protoLoader = require('@grpc/proto-loader')
const descriptor = require('protobufjs/ext/descriptor')

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

let count = 0

call.on('data', res => {
    if (res.list_services_response) {
        const names = res.list_services_response.service.map(s => s.name)
        
        count = names.length

        names.forEach(name => 
            call.write({host: 'localhost', file_containing_symbol: name})
        )
    }
    else if (res.file_descriptor_response) {
        if (--count == 0) {
            call.end()
        }

        res.file_descriptor_response.file_descriptor_proto
            .map(buf => descriptor.FileDescriptorProto.decode(buf))
            .forEach(d => {
                console.log(JSON.stringify(d, null, 2))
            })
    }
    else {
        console.log(res)
        call.end()
    }
})

call.write({host: 'localhost', list_services: ''})
