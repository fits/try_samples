
const express = require('express')
const app = express()

const { HTTP } = require('cloudevents')

app.use(express.json({type: [
    'application/json', 'application/cloudevents+json'
]}))

app.post('/', (req, res) => {
    const headers = req.headers
    const body = req.body

    console.log(`headers: ${JSON.stringify(headers)}, body: ${JSON.stringify(body)}`)

    const event = HTTP.toEvent({ headers, body })

    console.log(event.toJSON())

    res.end()
})

app.listen(3000, () => {
    console.log('started')
})
