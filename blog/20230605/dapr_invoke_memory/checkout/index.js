const http = require('http')

const server = http.createServer((req, res) => {
    res.end(`path:${req.url}`)
})

server.listen(3000)
