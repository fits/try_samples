
const http = require('http')

const server = http.createServer((req, res) => {
    console.log(req.headers)

    res.writeHead(200, {'Content-Type': 'text/plain'})
    res.end('ok')

})

server.listen(8080)
