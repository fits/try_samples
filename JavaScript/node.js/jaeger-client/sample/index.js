
const initJaegerTracer = require('jaeger-client').initTracer

const config = {
    serviceName: 'sample1-service',
    sampler: {
        type: 'const',
        param: 1
    }
}

const opts = {
    logger: {
        info(msg) {
            console.log('INFO', msg)
        },
        error(msg) {
            console.error(error)
        }
    }
}

const tracer = initJaegerTracer(config, opts)

const sample = () => {
    const span = tracer.startSpan('sample1')

    span.log({event: 'step1', data: 1})
    span.log({event: 'step2', data: 2})

    span.finish()
}

sample()

tracer.close(() => {
    console.log('close')
})
