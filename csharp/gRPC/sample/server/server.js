const protoLoader = require('@grpc/proto-loader')
const grpc = require('@grpc/grpc-js')
const { pki } = require('node-forge')

const createCred = () => {
    if (process.argv.length < 3) {
        return grpc.ServerCredentials.createInsecure()
    }

    const keys = pki.rsa.generateKeyPair({bits: 2048})

    const cert = pki.createCertificate()
    
    cert.publicKey = keys.publicKey
    
    cert.validity.notBefore = new Date()
    
    const expiryDate = new Date()
    expiryDate.setFullYear(expiryDate.getFullYear() + 1)
    
    cert.validity.notAfter = expiryDate
    
    const attrs = [
        { name: 'commonName', value: 'example.org' },
        { name: 'countryName', value: 'US' }
    ]
    
    cert.setSubject(attrs)
    cert.setIssuer(attrs)
    
    cert.sign(keys.privateKey)

    return grpc.ServerCredentials.createSsl(
        null,
        [{
            private_key: Buffer.from(pki.privateKeyToPem(keys.privateKey)),
            cert_chain: Buffer.from(pki.certificateToPem(cert)),
        }]
    )
}

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

server.bindAsync('0.0.0.0:5001', createCred(), (err, port) => {
    if (err != null) {
        console.error(err)
        return
    }

    console.log(`server started: port=${port}`)
})
