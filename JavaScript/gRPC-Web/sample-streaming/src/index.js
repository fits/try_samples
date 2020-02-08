
const { EventNotifyServiceClient } = require('./service_grpc_web_pb.js')
const { SubscribeRequest } = require('./service_pb.js')

const client = new EventNotifyServiceClient('http://localhost:8080')

const req = new SubscribeRequest()
req.setClientId('sample-client-1')
req.setDurableName('sample-client-1')

const stream = client.subscribe(req, {})

stream.on('data', d => {
    const event = d.toObject()

    console.log(event)

    document.getElementById('res').innerHTML += `<p>${JSON.stringify(event)}</p>`
})

stream.on('end', () => console.log('end'))
stream.on('error', (err) => console.error(err))
stream.on('status', (status) => console.log('status: ' + JSON.stringify(status)))
