
const axios = require('axios').default
const { HTTP, CloudEvent } = require('cloudevents')

const url = 'http://localhost:3000/'

const type = 'sample.created'
const source = 'example:sender-binary-js'
const data = {value: 'abc123'}

const event = new CloudEvent({ type, source, data })

console.log(event.toJSON())

const message = HTTP.binary(event)

console.log(message)

axios({
    method: 'post',
    url,
    data: message.body,
    headers: message.headers
}).catch(err => console.error(err))
