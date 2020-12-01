
const axios = require('axios').default
const { HTTP, CloudEvent } = require('cloudevents')

const url = 'http://localhost:3000/'

const type = 'sample.created'
const source = 'sample'
const data = {value: 'def456'}

const event = new CloudEvent({ type, source, data })

console.log(event.toJSON())

const message = HTTP.structured(event)

console.log(message)

axios({
    method: 'post',
    url,
    data: message.body,
    headers: message.headers
}).catch(err => console.error(err))
