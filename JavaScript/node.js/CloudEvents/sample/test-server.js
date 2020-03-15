const http = require('http')

http.createServer((req, res) => {
    console.log(`headers: ${JSON.stringify(req.headers)}`)

    let buf = ''

    req.on('data', ch => buf += ch)

    req.on('end', () => {
        console.log(`body: ${buf}`)
    })

    res.writeHead(200, { 'Content-Type': 'text/plain' })
    res.end('ok')

}).listen(8080)