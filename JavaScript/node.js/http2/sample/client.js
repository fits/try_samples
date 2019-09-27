
const http2 = require('http2')

const client = http2.connect('http://127.0.0.1:8080')

client.on('error', err => {
    console.error(err)
})

const req = client.request({
    ':method': 'POST',
    ':path': '/sample',
    'content-type': 'text/plain'
})

req.on('response', (hds, flg) => {
    console.log('*** response')
    console.log(hds)
    console.log(`flags: ${flg}`)
})

let data = ''

req.on('data', ch => data += ch)

req.on('end', () => {
    console.log('*** request end')

    console.log(data)
    client.close()
})

req.on('error', err => {
    console.log('*** request error')

    console.error(err)
})

req.end('abc')
