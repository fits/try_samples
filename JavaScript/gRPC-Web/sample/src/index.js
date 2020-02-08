
const { SampleServiceClient } = require('./sample_grpc_web_pb.js')
const { SampleRequest } = require('./sample_pb.js')

const client = new SampleServiceClient('http://localhost:8080')

console.log('access start')
console.log(client)

const req = new SampleRequest()
req.setMessage('sample message')

client.call(req, {}, (err, res) => {
    if (err) {
        console.error(err)
    }
    else {
		const msg = res.getMessage()

		console.log(msg)
		document.getElementById('res').innerHTML = msg
    }
})
