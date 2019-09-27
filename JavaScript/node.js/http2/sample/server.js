
const http2 = require('http2')

const server = http2.createServer()

server.on('stream', (stream, headers) => {
    console.log(headers)

    let data = ''

    stream.on('data', ch => data += ch)

    stream.on('end', () => {
        console.log(data)
    })

    stream.respond({
        ':status': 200,
        'content-type': 'text/plain'
    })

    stream.end('sample')
})

server.listen(8080)
