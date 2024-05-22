const protoLoader = require('@grpc/proto-loader')
const grpc = require('@grpc/grpc-js')

const pd = protoLoader.loadSync('../proto/sample.proto')
const proto = grpc.loadPackageDefinition(pd)

const server = new grpc.Server()

server.addService(proto.Process.service, {
    Exec(call, callback) {
        console.log(`called exec: ${JSON.stringify(call.request)}`)

        const cmd = call.request.command

        callback(null, { result: `ok-${cmd}` })
    },
})

const cred = grpc.ServerCredentials.createInsecure()

server.bindAsync('0.0.0.0:5001', cred, (err, port) => {
    if (err != null) {
        console.error(err)
        return
    }

    console.log(`server started: port=${port}`)
})
