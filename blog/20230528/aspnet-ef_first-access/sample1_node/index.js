const http = require('http')

const server = http.createServer((_req, res) => {
    res.end('nodejs-' + Date.now())
})

server.listen(3000)
