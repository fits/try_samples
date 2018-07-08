const vision = require('@google-cloud/vision')

const client = new vision.ImageAnnotatorClient()

const file = process.argv[2]

client
    .webDetection(file)
    .then(r => {
        console.log(JSON.stringify(r))
    })
    .catch(err => console.error('ERROR', err))

