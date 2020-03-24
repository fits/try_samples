'use strict'

const OpenTelemetry = require('@opentelemetry/api')
const { BasicTracerProvider, ConsoleSpanExporter, SimpleSpanProcessor, BatchSpanProcessor } = require('@opentelemetry/tracing')
const { JaegerExporter } = require('@opentelemetry/exporter-jaeger')

const exporter = new JaegerExporter({serviceName: 'sample-service'})

const provider = new BasicTracerProvider()

//provider.addSpanProcessor(new BatchSpanProcessor(exporter))
provider.addSpanProcessor(new SimpleSpanProcessor(exporter))
provider.addSpanProcessor(new SimpleSpanProcessor(new ConsoleSpanExporter()))

provider.register()

const tracer = OpenTelemetry.trace.getTracer('sample')

const mainSpan = tracer.startSpan('main')

const subSpan1 = tracer.startSpan('sub1', {parent: mainSpan})

subSpan1.setAttribute('value1', 12)
subSpan1.addEvent('sample-event1')

subSpan1.end()

const subSpan2 = tracer.startSpan('sub2', {parent: mainSpan})

subSpan2.addEvent('sample-event2_1', {version: 1})
subSpan2.addEvent('sample-event2_2', {version: 2})

subSpan2.end()

mainSpan.end()
