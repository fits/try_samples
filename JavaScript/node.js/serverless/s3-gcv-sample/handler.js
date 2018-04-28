'use strict'

const fetch = require('node-fetch')
const AWS = require('aws-sdk')
const s3 = new AWS.S3()

const detectText = obj => {
    const data = obj.Body.toString('base64')
    const endpoint = process.env.API_ENDPOINT

    const params = {
        requests: [{
            image: { content: data },
            features: [{ type: 'TEXT_DETECTION' }]
        }]
    }

    return fetch(endpoint, {
        method: 'POST',
        body: JSON.stringify(params)
    }).then(res => res.json())
}

const saveContent = (bucket, key, type, data) => s3.putObject({
    Bucket: bucket,
    Key: key,
    ContentType: type,
    Body: data
}).promise()

const validate = obj => {
    if (obj.error) {
        throw new Error(obj.error.message)
    }
    return obj
}

module.exports.detection = (event, context, callback) => {
    const bucket = event.Records[0].s3.bucket.name
    const key = decodeURIComponent(event.Records[0].s3.object.key.replace(/\+/g, " "))

    s3.getObject({ Bucket: bucket, Key: key }).promise()
        .then(detectText)
        .then(validate)
        .then(obj => 
            saveContent(bucket, `${key}.json`, 'application/json', 
                JSON.stringify(obj))
        )
        .then(res => callback(null, res))
        .catch(err => callback(err))
}
