const v1 = require('cloudevents-sdk/v1')

const event = v1.event()
                .type('com.example.sample.created')
                .source('/samples')
                .data('data-123')

const config = {
    method: 'POST',
    url: 'http://localhost:8080'
}

const binding = new v1.StructuredHTTPEmitter(config)

binding.emit(event)
    .then(r => console.log(r.data))
    .catch(err => console.error(err))
