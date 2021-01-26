
const { ConsoleLogger, LogLevel } = require('@opentelemetry/core')
const { NodeTracerProvider } = require('@opentelemetry/node')
const { SimpleSpanProcessor, ConsoleSpanExporter } = require('@opentelemetry/tracing')

const logger = new ConsoleLogger(LogLevel.DEBUG)
const provider = new NodeTracerProvider({ logger })

provider.addSpanProcessor(new SimpleSpanProcessor(new ConsoleSpanExporter()))

provider.register()

const http = require('http')

const server = http.createServer((req, res) => {
    res.writeHead(200, {'Content-Type': 'text/plain'})
    res.end('ok')
})

server.listen(8080)
