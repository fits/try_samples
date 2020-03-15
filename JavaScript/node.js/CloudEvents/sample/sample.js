const v1 = require('cloudevents-sdk/v1')

const event = v1.event()
                .type('com.example.sample.created')
                .source('/samples')
                .data('data-123')

console.log(event)

const payload = event.format()

console.log(payload)
