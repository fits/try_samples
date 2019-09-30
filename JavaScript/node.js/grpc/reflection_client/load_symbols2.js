
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

const merge = a => b => Object.fromEntries([a, b].map(Object.entries).flat())

const serverReflectionInfo = (f, g) => new Promise((resolve, revoke) => {
    const call = client.ServerReflectionInfo()

    call.on('error', err => {
        call.end()
        revoke(err)
    })

    call.on('data', res => {
        call.end()
        resolve( g(res) )
    })

    call.write( f({host: 'localhost'}) )
})

const listServices = () => serverReflectionInfo(
    merge({list_services: ''}),
    res => res.list_services_response.service.map(s => s.name)
)

const loadSymbol = name => serverReflectionInfo(
    merge({file_containing_symbol: name}),
    res => res.file_descriptor_response.file_descriptor_proto
                .map(buf => descriptor.FileDescriptorProto.decode(buf))
)

listServices()
    .then(names => 
        Promise.all(names.map(loadSymbol))
    )
    .then(ds => ds.forEach(d => console.log(JSON.stringify(d, null, 2))))
    .catch(err => console.error(err))
