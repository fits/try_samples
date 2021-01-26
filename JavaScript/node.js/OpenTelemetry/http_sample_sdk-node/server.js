
const opentelemetry = require('@opentelemetry/sdk-node')
const { ConsoleLogger, LogLevel } = require('@opentelemetry/core')
const { ConsoleSpanExporter } = require('@opentelemetry/tracing')

const logger = new ConsoleLogger(LogLevel.DEBUG)
const traceExporter = new ConsoleSpanExporter()

const sdk = new opentelemetry.NodeSDK({
    logger,
    traceExporter
})

const run = async () => {
    await sdk.start()

    const http = require('http')
    const process = require('process')

    const shutdown = () =>
        sdk.shutdown()
            .then(() => console.log('shutdown'))
            .catch(err => console.error(err))
            .finally(() => process.exit(0))
    
    process.on('SIGINT', shutdown)
    process.on('SIGTERM', shutdown)

    const server = http.createServer((req, res) => {
        res.writeHead(200, {'Content-Type': 'text/plain'})
        res.end('ok')
    })
    
    server.listen(8080)
}

run().catch(err => console.error(err))
